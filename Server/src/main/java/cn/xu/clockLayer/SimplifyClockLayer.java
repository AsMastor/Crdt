package cn.xu.clockLayer;

import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.SimplifyClock;

import java.util.concurrent.atomic.AtomicInteger;

public class SimplifyClockLayer implements ClockLayer{
    AtomicInteger nowGc;
    private NetLayer netLayer;

    public SimplifyClockLayer() {
        nowGc = new AtomicInteger(0);
    }

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {}

    @Override
    public void msgIn(String msgStr) {
        //System.out.println("Received Msg: " + msg.toString());
        Msg msg = new Msg(msgStr);
        SimplifyClock clock = (SimplifyClock)msg.getClock();
        int newGcb = nowGc.incrementAndGet();
        System.out.println(newGcb);
        clock.setGcb(newGcb);
        msgOut(msg.serialized());
    }

    @Override
    public void msgOut(String msgStr) {
        //System.out.println("Send Msg: " + msg.toString());
        netLayer.asyncSend(msgStr);
    }

    @Override
    public Clock generateNextClock() {
        return null;
    }
}
