package cn.xu.pojo.clock;

import cn.xu.config.Config;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiEdgeClock implements Clock {
    int nodeId, lc, edgeId, edgeGc, deliveryOrder;
    int[] gcas;

    // 反序列化
    public MultiEdgeClock(String clockStr) {
        String[] clockStrs = clockStr.split(Config.clockSplitter);
        int gcasLength = clockStrs.length - 5;
        gcas = new int[gcasLength];
        nodeId = Integer.parseInt(clockStrs[0]);
        lc = Integer.parseInt(clockStrs[1]);
        edgeId = Integer.parseInt(clockStrs[2]);
        edgeGc = Integer.parseInt(clockStrs[3]);
        deliveryOrder = Integer.parseInt(clockStrs[4]);
        for (int i = 0; i < gcasLength; i++) {
            gcas[i] = Integer.parseInt(clockStrs[i + 5]);
        }
    }

    @Override
    public String serialized() {
        StringBuilder sb = new StringBuilder(String.valueOf(nodeId));
        sb.append(Config.clockSplitter).append(String.valueOf(lc));
        sb.append(Config.clockSplitter).append(String.valueOf(edgeId));
        sb.append(Config.clockSplitter).append(String.valueOf(edgeGc));
        sb.append(Config.clockSplitter).append(String.valueOf(deliveryOrder));
        sb.append(Config.clockSplitter).append(String.valueOf(gcas[0]));
        for (int i = 1; i < gcas.length; i++) {
            sb.append(Config.clockSplitter).append(String.valueOf(gcas[i]));
        }
        return sb.toString();
    }

    @Override
    public ClockType getClockType() {
        return ClockType.MultiEdgeClock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<");
        sb.append(String.valueOf(nodeId)).append(",").append(String.valueOf(lc)).append("<")
                .append(String.valueOf(gcas[0]));
        for (int i = 1; i < gcas.length; i++) {
            sb.append(",").append(String.valueOf(gcas[i]));
        }
        sb.append(">").append(String.valueOf(edgeId)).append(",").append(String.valueOf(edgeGc))
                .append(",").append(String.valueOf(deliveryOrder)).append(">");
        return sb.toString();
    }
}
