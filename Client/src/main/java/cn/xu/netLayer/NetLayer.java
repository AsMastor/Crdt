package cn.xu.netLayer;

import cn.xu.backGround.BackGround;

public interface NetLayer {
    /**
     * 提供调用后台的对象
     */
    void setBackGround(BackGround backGround);

    /**
     * 提供异步发送消息的接口
     */
    void asyncSend(String msgStr);
}
