package cn.xu.clockLayer;

import cn.xu.backGround.BackGround;
import cn.xu.config.Config;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.SimplifyClock;
import cn.xu.netLayer.NetLayer;

import java.util.PriorityQueue;

public class SimplifyClockLayer implements ClockLayer{
    private final int nid;  // 这个节点的 id 保证全局不重复即可
    private  int nowLc, nowGc;
    private NetLayer netLayer;
    private CrdtObject crdtObject;
    private PriorityQueue<Msg> waitingQueue;    // 将还不能交付CRDT层的消息暂存在优先队列中

    public SimplifyClockLayer(int nid) {
        this.nid = nid;
        nowLc = 0;
        nowGc = 0;
        waitingQueue = new PriorityQueue<>((Msg m1, Msg m2)->
                ((SimplifyClock)(m1.getClock())).getGcb() - ((SimplifyClock)(m2.getClock())).getGcb()
        ); // 需要一个基于 Gcb 排序的小顶堆
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
        // 这里需要用CrdtObject来同步：消息到来的线程+CrdtObject的用户操作线程
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
        return ((SimplifyClock)(msg.getClock())).getGcb() == nowGc + 1;
    }

    /**
     * 将该消息发往CRDT层的具体逻辑
     */
    private void msgSendToCrdt(Msg msg) {
        nowGc++;
        if (msg.getClock().fromOwn(nid)) {
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
        return new SimplifyClock(nid, ++nowLc, nowGc, Config.emptyGcb);
    }
}
