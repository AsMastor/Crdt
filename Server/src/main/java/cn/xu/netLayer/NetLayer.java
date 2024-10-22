package cn.xu.netLayer;

import cn.xu.backGround.BackGround;

public interface NetLayer {
    void asyncSend(String msg);
    void setBackGround(BackGround backGround);
}
