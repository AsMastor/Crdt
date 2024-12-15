package cn.xu.pojo.clock;

import cn.xu.config.Config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiEdgeClock implements Clock {
    @JSONField(serialize=false)
    static ClockType clockType = ClockType.MultiEdgeClock;

    @JSONField(name = "N")
    int nodeId;
    @JSONField(name = "L")
    int lc;
    @JSONField(name = "E")
    int edgeId;
    @JSONField(name = "G")
    int edgeGc;
    @JSONField(name = "D")
    int deliveryOrder;
    @JSONField(name = "A")
    int[] gcas;

    // 反序列化
    public static MultiEdgeClock deSerialized(String clockStr) {
        return JSON.parseObject(clockStr, MultiEdgeClock.class);
    }

    @Override
    public String serialized() {
        return JSON.toJSONString(this);
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
