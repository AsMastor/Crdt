package cn.xu.pojo.clock;

public interface Clock {
    /**
     * 序列化
     */
    String serialized();

    /**
     * 获取时钟的种类
     */
    ClockType getClockType();

    /**
     * @return
     *  true: 该时钟来自本节点
     */
    boolean fromOwn(int thisNodeId);

    /**
     * @return
     *  0: c1 || c2
     *  1: c1 <- c2    即：当前时钟的因果序更大
     *  -1: c1 -> c2    即：当前时钟的因果序更小
     */
    int compare(Clock c2);

    /**
     * 判断当前中是否和 c2 时钟是同一个时钟
     *     向量时钟：用不到
     *     精简设置：nid 相等 且 lc 相等
     *     多端时钟：
     */
    boolean equals(Clock c2);

    /**
     * 当当前时钟和 c2 是同一个时钟时
     * 用 c2 替换掉当前时钟的内容
     */
    void replaceBy(Clock c2);

    /**
     * Lww 算法需要的全序，注意：在两个时钟并发时才能使用
     * 如果当前时钟 > c2 则返回true
     */
    boolean totalBigger(Clock c2);
}
