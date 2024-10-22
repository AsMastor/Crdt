package cn.xu.clockLayer;

import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;

public interface ClockLayer {
    void setNetLayer(NetLayer netLayer);
    void setCrdtLayer(CrdtObject crdtObject);
    void msgIn(String msgStr);
    void msgOut(String msgStr);
    Clock generateNextClock();
}
