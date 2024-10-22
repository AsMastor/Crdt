package cn.xu.pojo.clock;

public interface Clock {
    // 序列化
    String serialized();
    ClockType getClockType();
}
