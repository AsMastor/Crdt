package cn.xu.backGround;

import cn.xu.pojo.Msg;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.pojo.clock.Clock;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientBackGround implements BackGround {
    private final ThreadPoolExecutor threadPool;  // 线程池处理消息的去重工作
    private final Set<String> simDatabase;  // 用来模拟数据库的主键，执行去重逻辑
    private final LinkedBlockingQueue<Msg> msgAfterDeduplicate;    // 去重后的消息

    public ClientBackGround() {
        // 开启线程池处理消息去重逻辑
        threadPool = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        simDatabase = new HashSet<>();
        msgAfterDeduplicate = new LinkedBlockingQueue<>();
    }

    @Override
    public void msgIn(String msgStr) {
        threadPool.execute(()->{
            boolean isDuplicated = false;
            Msg msg = new Msg(msgStr);
            // 放入数据库的去重逻辑
            String clockStr = msg.getClock().serialized();
            if (simDatabase.contains(clockStr)) {
                isDuplicated = true;
            } else {
                simDatabase.add(clockStr);
            }
            if (!isDuplicated) {
                // 不是重复的消息，再交付给阻塞队列，然后续线程处理
                try {
                    msgAfterDeduplicate.put(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 该函数内单独启用一个线程，将数据往时钟层传
    @Override
    public void setClockLayer(ClockLayer clockLayer) {
        new Thread(()->{
            while (true) {
                Msg msg = null;
                // 从阻塞队列中获取消息，然后传给时钟层处理
                try {
                    msg = msgAfterDeduplicate.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    clockLayer.msgIn(msg);
                }
            }
        }).start();
    }

    /**
     * 这个方法是专门提供给向量时钟的，因为向量时钟无法单独判断来自自己的时钟，同时向量时钟不需要来自自己的时钟
     * 因此在BackGround中，需要将来自自己的时钟给筛掉
     * @param clock
     */
    @Override
    public void siftOutOwnClock(Clock clock) {
        simDatabase.add(clock.serialized());
    }
}
