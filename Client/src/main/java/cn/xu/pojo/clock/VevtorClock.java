package cn.xu.pojo.clock;

import cn.xu.config.Config;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VevtorClock implements Clock {
    int[] clock;
    int[] clock4Delivery;
    int nid;

    // 反序列化
    public VevtorClock(String clockStr) {
        String[] clockStrs = clockStr.split(Config.clockSplitter);
        int size = (clockStrs.length - 1) / 2;
        clock = new int[size];
        clock4Delivery = new int[size];
        for (int i = 0; i < size; i++) {
            clock[i] = Integer.parseInt(clockStrs[i]);
            clock4Delivery[i] = Integer.parseInt(clockStrs[size + i]);
        }
        nid = Integer.parseInt(clockStrs[clockStrs.length - 1]);
    }

    @Override
    public String serialized() {
        StringBuilder sb = new StringBuilder(String.valueOf(clock[0]));
        for (int i = 1; i < clock.length; i++) {
            sb.append(Config.clockSplitter).append(String.valueOf(clock[i]));
        }
        for (int i = 0; i < clock4Delivery.length; i++) {
            sb.append(Config.clockSplitter).append(String.valueOf(clock4Delivery[i]));
        }
        sb.append(Config.clockSplitter).append(String.valueOf(nid));
        return sb.toString();
    }

    @Override
    public ClockType getClockType() {
        return ClockType.VectorClock;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<<").append(clock[0]);
        for (int i = 1; i < clock.length; i++) {
            sb.append(", ").append(clock[i]);
        }
        sb.append(">,<").append(clock4Delivery[0]);;
        for (int i = 1; i < clock4Delivery.length; i++) {
            sb.append(", ").append(clock4Delivery[i]);
        }
        sb.append(">,").append(nid).append(">");
        return sb.toString();
    }
}
