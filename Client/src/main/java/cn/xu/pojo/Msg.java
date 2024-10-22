package cn.xu.pojo;

import cn.xu.pojo.clock.*;
import cn.xu.pojo.operation.Operation;
import cn.xu.config.Config;
import lombok.Data;

@Data
public class Msg {
    private Operation op;
    private Clock clock;

    public Msg(Operation op, Clock clock) {
        this.op = op;
        this.clock = clock;
    }

    // 序列化
    public Msg(String msgStr) {
        String[] msgStrs = msgStr.split(Config.msgSplitter);
        if (msgStrs.length == 3) {
            op = new Operation(msgStrs[0]);
            int clockType = Integer.parseInt(msgStrs[1]);
            if (clockType == ClockType.VectorClock.ordinal()) {
                clock = new VevtorClock(msgStrs[2]);
            } else if (clockType == ClockType.SimplifyClock.ordinal()) {
                clock = new SimplifyClock(msgStrs[2]);
            } else if (clockType == ClockType.MultiEdgeClock.ordinal()) {
                clock = new MultiEdgeClock(msgStrs[2]);
            } else {
                throw new RuntimeException("Illegal Msg Clock Type");
            }
        } else {
            throw new RuntimeException("Illegal Msg Number");
        }
    }

    public String serialized() {
        // op&1&clock
        return op.serialized().concat(Config.msgSplitter)
                .concat(String.valueOf(clock.getClockType().ordinal()))
                .concat(Config.msgSplitter).concat(clock.serialized());
    }

    @Override
    public String toString() {
        return "{".concat(op.toString()).concat("; ").concat(clock.toString()).concat("}");
    }
}
