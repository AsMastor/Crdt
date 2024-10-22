package cn.xu.pojo.clock;

public class MultiEdgeClock implements Clock {

    // 反序列化
    public MultiEdgeClock(String clockStr) {

    }

    @Override
    public String serialized() {
        return null;
    }

    @Override
    public ClockType getClockType() {
        return ClockType.MultiEdgeClock;
    }
}
