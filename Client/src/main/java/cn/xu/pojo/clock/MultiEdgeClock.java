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
}
