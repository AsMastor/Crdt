package cn.xu.pojo.clock;

import cn.xu.config.Config;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimplifyClock implements Clock {
    int nid, lc, gca, gcb;

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

    @Override
    public boolean fromOwn(int thisNodeId) {
        return nid == thisNodeId;
    }

    @Override
    public int compare(Clock c2) {
        SimplifyClock clock2 = (SimplifyClock) c2;
        if (this.gcb == Config.emptyGcb || clock2.gcb == Config.emptyGcb) {
            return 0;
        }
        if (this.nid == clock2.nid) {
            if (this.lc < clock2.lc) {
                return -1;
            }
            if (this.lc > clock2.lc) {
                return 1;
            }
            throw new RuntimeException("same clock error");
        }
        if (this.gcb <= clock2.gca) {
            return -1;
        }
        if (clock2.gcb <= this.gca) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Clock c2) {
        SimplifyClock clock2 = (SimplifyClock) c2;
        return this.nid == clock2.nid && this.lc == clock2.lc;
    }

    @Override
    public void replaceBy(Clock c2) {
        this.gcb = ((SimplifyClock) c2).gcb;
    }

    @Override
    public boolean totalBigger(Clock c2) {
        return this.nid < ((SimplifyClock) c2).nid;
    }

    @Override
    public String toString() {
        return "<".concat(String.valueOf(nid)).concat(",").concat(String.valueOf(lc)).concat(",")
                .concat(String.valueOf(gca)).concat(",").concat(String.valueOf(gcb)).concat(">");
    }
}
