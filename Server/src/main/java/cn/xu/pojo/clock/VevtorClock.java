package cn.xu.pojo.clock;

public class VevtorClock implements Clock {

    // 反序列化
    public VevtorClock(String clockStr) {

    }

    @Override
    public String serialized() {
        return null;
    }

    @Override
    public ClockType getClockType() {
        return ClockType.VectorClock;
    }
}
