package cn.xu.netLayer;

import cn.xu.backGround.BackGround;

public interface NetLayer {
    void asyncSend(String msg);
    void asyncSendToOtherServers(String msg);
    void setBackGround(BackGround backGround);
    void startSycAsyncSend(String msg);
}
