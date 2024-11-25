package cn.xu.clockLayer;

import cn.xu.backGround.BackGround;
import cn.xu.config.Config;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.MultiEdgeClock;

import java.util.PriorityQueue;

public class MultiEdgeClockLayer implements ClockLayer{
    private final int nodeId;  // 这个节点的 id 保证全局不重复即可
    private int lc;
    private int[] gcas;
    private PriorityQueue<Msg> waitingQueue;    // 将还不能交付CRDT层的消息暂存在优先队列中
    private int nowDeliveryOrder;

    private NetLayer netLayer;
    private CrdtObject crdtObject;

    public MultiEdgeClockLayer(int nodeId, int edgedServerNum) {
        this.nodeId = nodeId;
        this.lc = 0;
        this.gcas = new int[edgedServerNum];
        waitingQueue = new PriorityQueue<>((Msg m1, Msg m2) -> {
            return ((MultiEdgeClock) m1.getClock()).getDeliveryOrder() - ((MultiEdgeClock) m2.getClock()).getDeliveryOrder();
        });
        nowDeliveryOrder = 0;
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
    public void setBackGround(BackGround backGround) {
        return;
    }

    @Override
    public void msgIn(Msg msg) {
        // 这里需要暂存还没有达到“可以转发给数据结构层的消息”
        // 等到允许转发时，再给 CRDT 层传送消息
        // 这里需要用 CrdtObject 来同步：消息到来的线程 + CrdtObject 的用户操作线程
        synchronized (crdtObject) {
            if (canMsgSendToCrdt(msg)) {
                msgSendToCrdt(msg);
                // 查询等待队列能否传送消息
                while (!waitingQueue.isEmpty()) {
                    if (canMsgSendToCrdt(waitingQueue.peek())) {
                        msgSendToCrdt(waitingQueue.poll());
                    } else {
                        break;
                    }
                }
            } else {
                // 放入等待队列
                waitingQueue.add(msg);
            }
        }
    }

    /**
     * 判断该消息能否往CRDT层发送
     */
    private boolean canMsgSendToCrdt(Msg msg) {
        return ((MultiEdgeClock)(msg.getClock())).getDeliveryOrder() == nowDeliveryOrder + 1;
    }

    /**
     * 将该消息发往CRDT层的具体逻辑
     */
    private void msgSendToCrdt(Msg msg) {
        nowDeliveryOrder++;
        gcas[((MultiEdgeClock)(msg.getClock())).getEdgeId()]++;
        if (msg.getClock().fromOwn(nodeId)) {
            crdtObject.ownMsgIn(msg);
        } else {
            crdtObject.msgIn(msg);
        }
    }

    @Override
    public void msgOut(Msg msg) {
        netLayer.asyncSend(msg.serialized());
    }

    @Override
    public Clock generateNextClock() {
        // 这里数组需要深拷贝
        int[] newGcas = new int[gcas.length];
        for (int i = 0; i < gcas.length; i++) {
            newGcas[i] = gcas[i];
        }
        return new MultiEdgeClock(nodeId, ++lc, Config.Null, Config.Null, Config.Null, newGcas);
    }
}
