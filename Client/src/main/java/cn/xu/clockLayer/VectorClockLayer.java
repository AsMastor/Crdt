package cn.xu.clockLayer;

import cn.xu.backGround.BackGround;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.VevtorClock;

import java.util.LinkedList;
import java.util.ListIterator;

public class VectorClockLayer implements ClockLayer{
    private final int nid;    // 这个节点的id：取值从 0 —— nodeNum-1
    private final int nodeNum;    // 全局中所有节点的数量
    private final int[] nowClock;
    private final int[] nowClock4Delivery;
    private LinkedList<Msg> waitingList;    // 将还不能交付CRDT层的消息暂存在链表中
    private NetLayer netLayer;
    private CrdtObject crdtObject;
    private BackGround backGround;

    public VectorClockLayer(int thisNodeId, int nodeNum) {
        this.nid = thisNodeId;
        this.nodeNum = nodeNum;
        nowClock = new int[nodeNum];
        nowClock4Delivery = new int[nodeNum];
        waitingList = new LinkedList<>();
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
        this.backGround = backGround;
    }

    @Override
    public void msgIn(Msg msg) {
        // 这里需要暂存还没有达到“可以转发给数据结构层的消息”
        // 等到允许转发时，再给 CRDT 层传送消息
        // 这里需要用CrdtObject来同步：消息到来的线程+CrdtObject的用户操作线程
        synchronized (crdtObject) {
            if (canMsgSendToCrdt(msg)) {
                msgSendToCrdt(msg);
                // 查询等待链表能否传送消息
                ListIterator<Msg> it = waitingList.listIterator();
                while (it.hasNext()) {
                    Msg itMsg = it.next();
                    if (canMsgSendToCrdt(itMsg)) {
                        msgSendToCrdt(itMsg);
                        it.remove();
                    }
                }
            } else {
                // 放入等待队列
                waitingList.addLast(msg);
            }
        }
    }

    private boolean canMsgSendToCrdt(Msg msg) {
        int[] msgClock4Delivery = ((VevtorClock)msg.getClock()).getClock4Delivery();
        for (int i = 0; i < nodeNum; i++) {
            if (msgClock4Delivery[i] > nowClock4Delivery[i]) {
                return false;
            }
        }
        return true;
    }

    private void msgSendToCrdt(Msg msg) {
        int msgNodeId = ((VevtorClock) msg.getClock()).getNid();
        nowClock4Delivery[msgNodeId]++;
        int[] msgClock = ((VevtorClock) msg.getClock()).getClock();
        for (int i = 0; i < nodeNum; i++) {
            nowClock[i] = Math.max(nowClock[i], msgClock[i]);
        }
        crdtObject.msgIn(msg);
    }

    @Override
    public void msgOut(Msg msg) {
        backGround.siftOutOwnClock(msg.getClock());
        netLayer.asyncSend(msg.serialized());
    }

    @Override
    public Clock generateNextClock() {
        int[] newClock = new int[nodeNum];
        int[] newClock4Delivery = new int[nodeNum];
        nowClock[nid]++;
        for (int i = 0; i < nodeNum; i++) {
            newClock[i] = nowClock[i];
            newClock4Delivery[i] = nowClock4Delivery[i];
        }
        nowClock4Delivery[nid]++;
        return new VevtorClock(newClock, newClock4Delivery, nid);
    }
}
