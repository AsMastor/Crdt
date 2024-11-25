package cn.xu.backGround;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.pojo.Msg;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerBackGround implements BackGround {
    ClockLayer clockLayer;
    private final ThreadPoolExecutor threadPool;  // 线程池处理消息的去重工作
    private final Set<String> simDatabase;  // 用来模拟数据库的主键，执行去重逻辑

    public ServerBackGround() {
        // 开启线程池处理消息去重逻辑+消息转发
        threadPool = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        simDatabase = new HashSet<>();
    }

    @Override
    public void msgIn(String msgStr) {
        synchronized (this) {
            threadPool.execute(()->{
                boolean isDuplicated = false;
                // 放入数据库的去重逻辑
                String clockStr = Msg.getClockStr(msgStr);
                if (simDatabase.contains(clockStr)) {
                    isDuplicated = true;
                } else {
                    simDatabase.add(clockStr);
                }
                if (!isDuplicated) {
                    // 不是重复的消息，再后续处理
                    clockLayer.msgIn(msgStr);
                }
            });
        }
    }

    @Override
    public void setClockLayer(ClockLayer clockLayer) {
        this.clockLayer = clockLayer;
    }
}
