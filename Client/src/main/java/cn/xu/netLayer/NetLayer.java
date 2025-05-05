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

    /**
     * 客户端新加入系统时，请求边缘服务器的数据同步
     */
    void startSyc();
}
