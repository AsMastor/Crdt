package cn.xu.clockLayer;

import cn.xu.backGround.BackGround;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.netLayer.NetLayer;

public interface ClockLayer {
    /**
     * 提供调用网络层的对象
     */
    void setNetLayer(NetLayer netLayer);

    /**
     * 提供调用CRDT数据结构层的对象
     */
    void setCrdtLayer(CrdtObject crdtObject);

    /**
     * Only For Vector Clock！
     * 这个方法是专门提供给向量时钟的，因为向量时钟无法单独判断来自自己的时钟，同时向量时钟不需要来自自己的时钟
     * 因此在BackGround中，需要将来自自己的时钟给筛掉
     */
    void setBackGround(BackGround backGround);

    /**
     * 从BackGround中收到消息
     */
    void msgIn(Msg msg);

    /**
     * 从CRDT层收到消息，负责发送
     */
    void msgOut(Msg msg);

    /**
     * 提供给CRDT数据结构层，生成下一个时钟的接口
     */
    Clock generateNextClock();
}
