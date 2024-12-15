package cn.xu.pojo.clock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class TreeClock implements Clock{
    @JSONField(serialize=false)
    static ClockType clockType = ClockType.TreeClock;

    // 反序列化
    public static TreeClock deSerialized(String clockStr) {
        return JSON.parseObject(clockStr, TreeClock.class);
    }

    // 序列化
    @Override
    public String serialized() {
        return JSON.toJSONString(this);
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }

    @Override
    public boolean fromOwn(int thisNodeId) {
        return false;
    }

    @Override
    public int compare(Clock c2) {
        return 0;
    }

    @Override
    public boolean equals(Clock c2) {
        return false;
    }

    @Override
    public void replaceBy(Clock c2) {

    }

    @Override
    public boolean totalBigger(Clock c2) {
        return false;
    }
}
