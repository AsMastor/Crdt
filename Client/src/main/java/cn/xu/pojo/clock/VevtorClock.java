package cn.xu.pojo.clock;

import cn.xu.config.Config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VevtorClock implements Clock {
    @JSONField(serialize=false)
    static ClockType clockType = ClockType.VectorClock;

    @JSONField(name = "C")
    int[] clock;
    @JSONField(name = "D")
    int[] clock4Delivery;
    @JSONField(name = "N")
    int nid;

    // 反序列化
    public static VevtorClock deSerialized(String clockStr) {
        return JSON.parseObject(clockStr, VevtorClock.class);
    }

    @Override
    public String serialized() {
        return JSON.toJSONString(this);
    }

    @Override
    public ClockType getClockType() {
        return clockType;
    }

    /**
     * 向量时钟：在BackGround中将本地产生的时钟判断为“重复消息”给剔除掉了
     * 因此能进入CRDTObject层的向量时钟都 “不是来自本地产生的”
     */
    @Override
    public boolean fromOwn(int thisNodeId) {
        return false;
    }

    /**
     * @return  对于 向量时钟 来说
     *  0: c1 || c2
     *  0: c1 == c2    向量时钟在 backGround 去重了时钟，不会用到这种情况
     *  1: c1 <- c2    即：当前时钟的因果序更大
     *  -1: c1 -> c2    即：当前时钟的因果徐更小
     */
    @Override
    public int compare(Clock c2) {
        int preCompare = 0;
        int[] c2Clock = ((VevtorClock) c2).clock;
        if (clock.length != c2Clock.length) {
            throw new RuntimeException("Vector Clock Cant Compare");
        }
        for (int i = 0; i < clock.length; i++) {
            int nowCompare = Integer.compare(clock[i], c2Clock[i]);
            // 两个时钟：并发关系
            if (preCompare * nowCompare < 0) {
                return 0;
            }
            if (preCompare == 0) {
                preCompare = nowCompare;
            }
        }
        return preCompare;
    }

    /**
     * 向量时钟：根本用不到这个函数
     */
    @Override
    public boolean equals(Clock c2) {
        return false;
    }

    /**
     * 向量时钟：根本用不到这个函数
     */
    @Override
    public void replaceBy(Clock c2) {}

    @Override
    public boolean totalBigger(Clock c2) {
        return this.nid < ((VevtorClock) c2).nid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<<").append(clock[0]);
        for (int i = 1; i < clock.length; i++) {
            sb.append(",").append(clock[i]);
        }
        sb.append(">,<").append(clock4Delivery[0]);;
        for (int i = 1; i < clock4Delivery.length; i++) {
            sb.append(",").append(clock4Delivery[i]);
        }
        sb.append(">,").append(nid).append(">");
        return sb.toString();
    }
}
