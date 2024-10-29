package cn.xu.pojo.clock;

import cn.xu.config.Config;
import lombok.Data;

@Data
public class SimplifyClock implements Clock {
    int nid, lc, gca, gcb;

    public SimplifyClock(int nid, int lc, int gca, int gcb) {
        this.nid = nid;
        this.lc = lc;
        this.gca = gca;
        this.gcb = gcb;
    }

    // 反序列化
    public SimplifyClock(String clockStr) {
        String[] clockStrs = clockStr.split(Config.clockSplitter);
        if (clockStrs.length == 4) {
            nid = Integer.parseInt(clockStrs[0]);
            lc = Integer.parseInt(clockStrs[1]);
            gca = Integer.parseInt(clockStrs[2]);
            gcb = Integer.parseInt(clockStrs[3]);
        } else {
            throw new RuntimeException("Illegal SimplifyClock");
        }
    }

    @Override
    public String serialized() {
        return String.valueOf(nid).concat(Config.clockSplitter).concat(String.valueOf(lc)).concat(Config.clockSplitter)
                .concat(String.valueOf(gca)).concat(Config.clockSplitter).concat(String.valueOf(gcb));
    }

    @Override
    public ClockType getClockType() {
        return ClockType.SimplifyClock;
    }

    public void setGcb(int gcb) {
        this.gcb = gcb;
    }

    @Override
    public String toString() {
        return "<".concat(String.valueOf(nid)).concat(",").concat(String.valueOf(lc)).concat(",")
                .concat(String.valueOf(gca)).concat(",").concat(String.valueOf(gcb)).concat(">");
    }
}
