package cn.xu.pojo.clock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;

@Data
@AllArgsConstructor
public class TreeClock implements Clock{
    @JSONField(serialize=false)
    static ClockType clockType = ClockType.TreeClock;

    @JSONField(name = "R")
    int rootId; // 根id同时也是当前节点id
    @JSONField(name = "C")
    int[] clk;
    @JSONField(name = "A")
    int[] aClk;
    @JSONField(name = "T")
    LinkedList<Integer>[] treeStruct;
    @JSONField(name = "P")
    int[] parent;

    // 反序列化
    public static TreeClock deSerialized(String clockStr) {
        return JSON.parseObject(clockStr, TreeClock.class);
    }

    // 序列化
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
        return false;
    }

    /**
     * @return  对于 树时钟 来说
     *  0: c1 || c2
     *  0: c1 == c2    树时钟在 backGround 去重了时钟，不会用到这种情况
     *  1: c1 <- c2    即：当前时钟的因果序更大
     *  -1: c1 -> c2    即：当前时钟的因果徐更小
     */
    @Override
    public int compare(Clock c2) {
        int[] c2Clk = ((TreeClock) c2).clk;
        int c2RootId = ((TreeClock) c2).rootId;
        if (clk[c2RootId] >= c2Clk[c2RootId]) {
            return 1;
        }
        if (c2Clk[rootId] >= clk[rootId]) {
            return -1;
        }
        return 0;
    }

    /**
     * 树时钟：根本用不到这个函数
     */
    @Override
    public boolean equals(Clock c2) {
        return false;
    }

    /**
     * 树时钟：根本用不到这个函数
     */
    @Override
    public void replaceBy(Clock c2) {}

    @Override
    public boolean totalBigger(Clock c2) {
        return this.rootId < ((TreeClock) c2).rootId;
    }

    @Override
    public String toString() {
        return serialized();
    }
}
