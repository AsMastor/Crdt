package cn.xu.pojo.clock;

public enum ClockType {
    VectorClock(0),
    SimplifyClock(1),
    MultiEdgeClock(2),
    TreeClock(3);

    ClockType(int i) {}
}
