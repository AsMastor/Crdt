package cn.xu.clockLayer;

import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.clock.Clock;

public class MultiEdgeClockLayer implements ClockLayer{
    @Override
    public void setNetLayer(NetLayer netLayer) {

    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {}

    @Override
    public void msgIn(String msgStr) {

    }

    @Override
    public void msgOut(String msgStr) {

    }

    @Override
    public Clock generateNextClock() {
        return null;
    }
}
