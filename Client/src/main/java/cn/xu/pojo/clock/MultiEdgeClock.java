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
        return clockType;
    }

    @Override
    public boolean fromOwn(int thisNodeId) {
        return thisNodeId == nodeId;
    }

    @Override
    public int compare(Clock c2) {
        MultiEdgeClock clock2 = (MultiEdgeClock) c2;
        if (this.nodeId == clock2.nodeId) {
            if (this.lc < clock2.lc) {
                return -1;
            }
            if (this.lc > clock2.lc) {
                return 1;
            }
            throw new RuntimeException("same clock error");
        }
        if (this.edgeGc != Config.Null) {
            if (this.edgeGc <= clock2.gcas[this.edgeId]) {
                return -1;
            }
        }
        if (clock2.edgeGc != Config.Null) {
            if (clock2.edgeGc <= this.gcas[clock2.edgeId]) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Clock c2) {
        MultiEdgeClock clock2 = (MultiEdgeClock) c2;
        return this.nodeId == clock2.nodeId && this.lc == clock2.lc;
    }

    @Override
    public void replaceBy(Clock c2) {
        MultiEdgeClock clock2 = (MultiEdgeClock) c2;
        this.edgeId = clock2.edgeId;
        this.edgeGc = clock2.edgeGc;
        this.deliveryOrder = clock2.deliveryOrder;
    }

    @Override
    public boolean totalBigger(Clock c2) {
        return this.nodeId < ((MultiEdgeClock) c2).nodeId;
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
