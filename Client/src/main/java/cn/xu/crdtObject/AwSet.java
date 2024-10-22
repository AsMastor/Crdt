package cn.xu.crdtObject;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.pojo.Msg;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class AwSet implements CrdtObject{
    private static final String PRESENT = "";
    private final MvMap mvMap;

    public AwSet() {
        mvMap = new MvMap();
    }

    @Override
    public void setCountDownLatch(CountDownLatch latch) {
        mvMap.setCountDownLatch(latch);
    }

    @Override
    public void setClockLayer(ClockLayer clockLayer) {
        mvMap.setClockLayer(clockLayer);
    }

    @Override
    public void msgIn(Msg msg) {
        mvMap.msgIn(msg);
    }

    @Override
    public void ownMsgIn(Msg msg) {
        mvMap.ownMsgIn(msg);
    }

    public void add(String key) {
        mvMap.add(key, PRESENT);
    }

    public boolean remove(String key) {
        return mvMap.remove(key);
    }

    public Set<String> query() {
        return mvMap.query().keySet();
    }

    @Override
    public String toString() {
        return mvMap.toString();
    }
}
