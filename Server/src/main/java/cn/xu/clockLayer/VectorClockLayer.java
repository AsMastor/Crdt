package cn.xu.clockLayer;

import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.clock.Clock;

import java.util.concurrent.atomic.AtomicInteger;

public class VectorClockLayer implements ClockLayer{
    private NetLayer netLayer;
    private AtomicInteger msgCnt;

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
        msgCnt = new AtomicInteger(0);
    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {}

    @Override
    public void msgIn(String msgStr) {
        //System.out.println("Received Msg: " + msg.toString());
        System.out.println(msgCnt.incrementAndGet());
        msgOut(msgStr);
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
