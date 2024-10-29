package cn.xu.clockLayer;

import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.SimplifyClock;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class SimplifyClockLayer implements ClockLayer{
    private final Map<Integer, Integer> nid2Lc; // 记录每个端节点的nid以及lc，保证每个端节点的消息能FIFO
    private final AtomicInteger nowGc;
    private NetLayer netLayer;

    public SimplifyClockLayer() {
        nowGc = new AtomicInteger(0);
        nid2Lc = new HashMap<>();
    }

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {}

    @Override
    public void msgIn(String msgStr) {
        //System.out.println("Received Msg: " + msg.toString());
        Msg msg = new Msg(msgStr);
        SimplifyClock clock = (SimplifyClock)msg.getClock();
        int newGcb;

        /********************************************************************************
         * 非常核心的一段代码
         * 目的：
         *     保证同一个nid来的消息，依据lc的顺序FIFO的获取gcb
         *     即：对于 x.nid == y.nid，若 x.lc < y.lc 则一定有 x.gcb < y.gcb；反之亦成立
         * 实现策略：
         *     通过循环等待的方式，等待合适的nid-lc后才获取gcb
         *     bug: 若每个线程都卡住了循环等待，则造成了死锁
         *     TODO：循环太多，需要释放本线程
         ********************************************************************************/
        if (clock.getLc() == 1) {
            // 只有第一个消息能创建新的key-value值
            newGcb = nowGc.incrementAndGet();
            // 最后更新记录
            nid2Lc.put(clock.getNid(), clock.getLc());
        } else {
            int cnt = 0;
            int maxCnt = 10;    // 循环 maxCnt 后还不能执行则放回线程池的阻塞队列当中排队
            Integer expectLc;
            // 先等待该key值的存在
            while (!nid2Lc.containsKey(clock.getNid())) {
                cnt++;
                if (cnt >= maxCnt) {
                    // TODO:循环太多，需要释放本线程
                }
            }
            // 再等待，期待的key-value值
            expectLc = nid2Lc.get(clock.getNid());
            while (!expectLc.equals(clock.getLc() - 1)) {
                cnt++;
                if (cnt >= maxCnt) {
                    // TODO:循环太多，需要释放本线程
                }
            }
            // 最后获取到GC值
            newGcb = nowGc.incrementAndGet();
            // 最后更新记录
            nid2Lc.put(clock.getNid(), clock.getLc());
        }

        System.out.println(newGcb);
        clock.setGcb(newGcb);
        msgOut(msg.serialized());
    }

    @Override
    public void msgOut(String msgStr) {
        //System.out.println("Send Msg: " + msg.toString());
        netLayer.asyncSend(msgStr);
    }

    @Override
    public Clock generateNextClock() {
        return null;
    }
}
