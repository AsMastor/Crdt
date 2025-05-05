package cn.xu.crdtObject;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;

import java.util.concurrent.CountDownLatch;

public interface CrdtObject {
    /**
     * 提供 CountDownLatch 以此来保证获取到所有操作结束时间
     */
    void setCountDownLatch(CountDownLatch latch);

    /**
     * 提供调用时钟层的对象
     */
    void setClockLayer(ClockLayer clockLayer);

    /**
     * 时钟层将消息分为：来自本地产生的消息、来自其他节点产生的消息
     * 本方法表示处理来自其他节点产生的消息
     */
    void msgIn(Msg msg);

    /**
     * 本方法表示处理来自本节点产生的消息
     * 特别的：Vector Clock下，本方法可以无视（已经在BackGround中被过滤掉了）
     */
    void ownMsgIn(Msg msg);
}
