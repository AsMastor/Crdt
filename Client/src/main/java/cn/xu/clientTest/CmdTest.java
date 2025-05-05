package cn.xu.clientTest;

import cn.xu.crdtObject.*;
import cn.xu.factory.*;
import cn.xu.utils.CmdLine;
import cn.xu.utils.TestUtils;
import cn.xu.utils.TimeCounter;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class CmdTest {
    public static void testMultiEdgeLogList(int nodeId, int eId, int edgeNum) {
        CountDownLatch latch = new CountDownLatch(600);
        TimeCounter tc = new TimeCounter();
        Factory factory = new MultiEdgeClockFactory(nodeId, eId, edgeNum, new Random(), new Semaphore(0));
        LogList logList = factory.buildLogList();
        logList.setCountDownLatch(latch);
        tc.start();
        new Thread(()->{
            showCrdtObject(logList, latch, tc);
        }).start();
        CmdLine.run(logList);
    }

    public static void testMultiEdgeAwSet(int nodeId, int eId, int edgeNum) {
        CountDownLatch latch = new CountDownLatch(600);
        TimeCounter tc = new TimeCounter();
        Factory factory = new MultiEdgeClockFactory(nodeId, eId, edgeNum, new Random(), new Semaphore(0));
        AwSet awSet = factory.buildAwSet();
        awSet.setCountDownLatch(latch);
        tc.start();
        new Thread(()->{
            showCrdtObject(awSet, latch, tc);
        }).start();
        CmdLine.run(awSet);
    }

    public static void testMultiEdgeLwwMap(int nodeId, int eId, int edgeNum) {
        CountDownLatch latch = new CountDownLatch(600);
        TimeCounter tc = new TimeCounter();
        Factory factory = new MultiEdgeClockFactory(nodeId, eId, edgeNum, new Random(), new Semaphore(0));
        LwwMap lwwMap = factory.buildLwwMap();
        lwwMap.setCountDownLatch(latch);
        tc.start();
        new Thread(()->{
            showCrdtObject(lwwMap, latch, tc);
        }).start();
        CmdLine.run(lwwMap);
    }

    private static void showCrdtObject(CrdtObject object, CountDownLatch latch, TimeCounter tc) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 记录当所有操作同步完成的结束时间
        tc.end();
        System.out.println(tc.time());
    }
}
