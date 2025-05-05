package cn.xu.clockLayer;

import cn.xu.config.Config;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.utils.WidthCnt;
import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class MultiEdgeClockLayer implements ClockLayer{
    private final int edgeId;
    private final Map<Integer, Integer> nid2Lc; // 记录每个端节点的nid以及lc，保证每个端节点的消息能FIFO
    private final Map<Integer, Integer> edgeNode2Pre;   // 记录每个边服务器的Id，以及其对应的已经接受的gc，保证每个边服务器的消息能FIFO
    private final AtomicInteger nowDeliveryOrder;   // 记录递交顺序
    private final LinkedList<Msg> waitingQueue;  // 记录等待的消息
    public WidthCnt cnt;

    private CrdtObject crdtObject;
    private NetLayer netLayer;

    public MultiEdgeClockLayer(int edgeId) {
        this.edgeId = edgeId;
        nid2Lc = new HashMap<>();
        edgeNode2Pre = new HashMap<>();
        nowDeliveryOrder = new AtomicInteger(0);
        waitingQueue = new LinkedList<>();
    }

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {
        this.crdtObject = crdtObject;
    }

    @Override
    public void msgIn(String msgStr) {
        Msg msg = new Msg(msgStr);
        handleMsg(msg);
        // 遍历等待队列，并找出能继续递交的消息
        traverseWaitingList();
    }

    private void handleMsg(Msg msg) {
        MultiEdgeClock clock = (MultiEdgeClock) msg.getClock();
        if (satisfyCausality(clock)) {
            msgSend(clock, msg);
        } else {
            cantSend(msg);
        }
    }

    /**
     * 判断该消息是否满足因果序，即能否被转发出去
     */
    private boolean satisfyCausality(MultiEdgeClock clock) {
        // 先判断该消息是本边缘服务器下的客户端，还是其他边缘服务器的转发
        if (clock.getEdgeId() == Config.Null || clock.getEdgeId() == edgeId) {  // 是来自本边缘服务器下的客户端
            return satisfyCausalityFromOwn(clock);
        } else {    // 是来自其他边缘服务器的转发
            return satisfyCausalityFromOthers(clock);
        }
    }

    /**
     * 将该消息转发出去
     */
    private void msgSend(MultiEdgeClock clock, Msg msg) {
        // 先判断该消息是本边缘服务器下的客户端，还是其他边缘服务器的转发
        if (clock.getEdgeId() == Config.Null || clock.getEdgeId() == edgeId) {  // 是来自本边缘服务器下的客户端
            localMsgSend(clock, msg);
        } else {    // 是来自其他边缘服务器的转发
            othersMsgSend(clock, msg);
        }
    }

    /**
     * 判断该本地 clock 是否满足因果次序
     */
    private boolean satisfyCausalityFromOwn(MultiEdgeClock clock) {
        int nowLc = nid2Lc.getOrDefault(clock.getNodeId(), 0);
        return clock.getLc() == nowLc + 1;
    }

    /**
     * 判断该外地 clock 是否满足因果次序
     */
    private boolean satisfyCausalityFromOthers(MultiEdgeClock clock) {
        // 首先保证FIFO
        if (edgeNode2Pre.getOrDefault(clock.getEdgeId(), 0) != clock.getEdgeGc() - 1) {
            return false;
        }
        // 然后保证因果序
        int[] gcas = clock.getGcas();
        for (int i = 0; i < gcas.length; i++) {
            if (gcas[i] <= edgeNode2Pre.getOrDefault(i, 0)) {   // 本位满足
                continue;
            } else { // 不满足
                return false;
            }
        }
        return true;
    }

    /**
     * 能发的本地消息的处理办法
     */
    private void localMsgSend(MultiEdgeClock clock, Msg msg) {
        synchronized (this) {
            // 更新 nid2Lc 记录
            nid2Lc.put(clock.getNodeId(), clock.getLc());
            // 添加 edgeId 消息
            clock.setEdgeId(edgeId);
            // 更新并获取 edgeGc
            int newGc = edgeNode2Pre.getOrDefault(clock.getEdgeId(), 0) + 1;
            edgeNode2Pre.put(clock.getEdgeId(), newGc);
            // 更新并获取 deliveryOrder
            int newDeliveryOrder = nowDeliveryOrder.incrementAndGet();
            System.out.println("Server " + edgeId + ": " + newDeliveryOrder);
            // 更新时钟，并发送消息
            setNewClock(clock, newGc, newDeliveryOrder);
            // 传到数据结构层，并由数据结构层调用转发
            crdtObject.ownMsgIn(msg);
        }
    }

    /**
     * 能发的外地消息的处理办法
     */
    private void othersMsgSend(MultiEdgeClock clock, Msg msg) {
        synchronized (this) {
            // 更新并获取 deliveryOrder, 并更新时钟
            int newDeliveryOrder = nowDeliveryOrder.incrementAndGet();
            clock.setDeliveryOrder(newDeliveryOrder);
            System.out.println("Server " + edgeId + ": " + newDeliveryOrder);
            // 更新 edgeGc
            edgeNode2Pre.put(clock.getEdgeId(), edgeNode2Pre.getOrDefault(clock.getEdgeId(), 0) + 1);
            // 传到数据结构层，并由数据结构层调用转发
            crdtObject.msgIn(msg);
        }
    }

    /**
     * 遍历等待队列，查询是否有消息可以递交
     */
    public void traverseWaitingList() {
        // 遍历等待队列，并找出能继续递交的消息
        Msg canDeliveryMsg = null;
        synchronized (this) {
            Iterator<Msg> iterator = waitingQueue.iterator();
            while (iterator.hasNext()) {
                Msg tempMsg = iterator.next();
                if (satisfyCausality((MultiEdgeClock) tempMsg.getClock())) {
                    canDeliveryMsg = tempMsg;
                    iterator.remove();
                    break;
                }
            }
        }
        if (canDeliveryMsg != null) {
            msgSend((MultiEdgeClock) canDeliveryMsg.getClock(), canDeliveryMsg);
            traverseWaitingList();
        }
        // TODO:Test
        for (Msg msg : waitingQueue) {
            System.out.print(((MultiEdgeClock)msg.getClock())+ " ");
        }
        System.out.println("");
        System.out.println(edgeNode2Pre);
    }

    /**
     * 不能发的消息的处理办法
     */
    private void cantSend(Msg msg) {
        synchronized (this) {
            waitingQueue.addLast(msg);
        }
    }

    private void setNewClock(MultiEdgeClock clock, int newGc, int newDeliveryOrder) {
        clock.setEdgeGc(newGc);
        clock.setDeliveryOrder(newDeliveryOrder);
    }

    @Override
    public void msgOut(String msgStr) {
        netLayer.asyncSend(msgStr);
        if (cnt != null) {
            cnt.add(msgStr.length());
        }
    }

    /**
     * 转发给其他边服务器的消息
     */
    public void msgOut2Edge(String msgStr) {
        netLayer.asyncSendToOtherServers(msgStr);
    }

    @Override
    public Clock generateNextClock() {
        return null;
    }
}
