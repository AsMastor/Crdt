package cn.xu.backGround;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.pojo.clock.Clock;

public interface BackGround {
    /**
     * 提供调用时钟层的对象
     */
    void setClockLayer(ClockLayer clockLayer);

    /**
     * 收到来自网络层的消息，负责处理后转发给时钟层
     */
    void msgIn(String msg);

    /**
     * Only For Vector Clock！
     * 这个方法是专门提供给向量时钟的，因为向量时钟无法单独判断来自自己的时钟，同时向量时钟不需要来自自己的时钟
     * 因此在BackGround中，需要将来自自己的时钟给筛掉
     */
    void siftOutOwnClock(Clock clock);
}
