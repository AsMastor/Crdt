package cn.xu.pojo;

import cn.xu.config.Config;
import cn.xu.pojo.clock.*;
import cn.xu.pojo.operation.Operation;
import lombok.Data;

@Data
public class Msg {
    private Operation op;   // 当 op 为 null 时，表示该 Msg 被无效化了
    private Clock clock;

    public Msg(Operation op, Clock clock) {
        this.op = op;
        this.clock = clock;
    }

    // 序列化
    public Msg(String msgStr) {
        String[] msgStrs = msgStr.split(Config.msgSplitter);
        if (msgStrs[0].equals(Config.invalidSymbol)) {
            int edgeId = Integer.parseInt(msgStrs[1]);
            int edgeGc = Integer.parseInt(msgStrs[2]);
            int deliveryOrder = Integer.parseInt(msgStrs[3]);
            clock = new MultiEdgeClock(Config.Null, Config.Null, edgeId, edgeGc, deliveryOrder, new int[]{0, 0 ,0});
            op = null;
            return;
        }
        if (msgStrs.length == 3) {
            op = new Operation(msgStrs[0]);
            int clockType = Integer.parseInt(msgStrs[1]);
            if (clockType == ClockType.VectorClock.ordinal()) {
                clock = new VevtorClock(msgStrs[2]);
            } else if (clockType == ClockType.SimplifyClock.ordinal()) {
                clock = new SimplifyClock(msgStrs[2]);
            } else if (clockType == ClockType.MultiEdgeClock.ordinal()) {
                clock = MultiEdgeClock.deSerialized(msgStrs[2]);
            } else {
                throw new RuntimeException("Illegal Msg Clock Type");
            }
        } else {
            throw new RuntimeException("Illegal Msg Number");
        }
    }

    public void invalid() {
        op = null;
    }

    public String serialized() {
        if (op == null) {
            MultiEdgeClock clock1 = (MultiEdgeClock) clock;
            return Config.invalidSymbol
                    .concat(Config.msgSplitter).concat(String.valueOf(clock1.getEdgeId()))
                    .concat(Config.msgSplitter).concat(String.valueOf(clock1.getEdgeGc()))
                    .concat(Config.msgSplitter).concat(String.valueOf(clock1.getDeliveryOrder()));
        }
        // op&1&clock
        return op.serialized().concat(Config.msgSplitter)
                .concat(String.valueOf(clock.getClockType().ordinal()))
                .concat(Config.msgSplitter).concat(clock.serialized());
    }

    public Clock getClock() {
        return clock;
    }

    public static String getClockStr(String msgStr) {
        String[] msgStrs = msgStr.split(Config.msgSplitter);
        return msgStrs[2];
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public String toString() {
        if (op == null) {
            return clock.toString();
        }
        return "{".concat(op.toString()).concat("; ").concat(clock.toString()).concat("}");
    }
}
