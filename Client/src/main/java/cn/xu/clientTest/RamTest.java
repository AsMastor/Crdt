package cn.xu.clientTest;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.factory.Factory;
import cn.xu.factory.MultiEdgeClockFactory;
import cn.xu.factory.TreeClockFactory;
import cn.xu.factory.VectorClockFactory;
import cn.xu.utils.IOUtils;
import cn.xu.utils.TestUtils;

import java.util.Random;
import java.util.concurrent.*;

public class RamTest {
    // 边缘服务器下的设置
    static int edgeId;          // 所属的边缘服务器id
    static int mqttServerId;    // 所属的mqtt转发中心（和边缘服务器id一致）
    // 客户端组设置
    static int clientGroupNum;  // 整个系统中客户端组的数量
    static int clientGroupId;   // 当前客户端组的用户id范围：clientGroupId * clientNum ~
    // 基础设置
    static int clientNum;   // 一个端服务器下的客户端数量
    static int throughPut;    // 所有客户端总共的吞吐量
    // 可计算出的数据
    static int opNum;  // 一个客户端执行的操作数量
    static int sleepTime = 10;    // 一个客户端在每个操作间隙的停顿时间
    // 概率设置
    static int sleepTimeRange = 0;
    static int addProbability = 8;
    static int rmvProbability = 2;
    static int charRange = 26;
    // random 和 线程池
    static Random mainRandom = new Random(System.currentTimeMillis());
    static ThreadPoolExecutor threadPool;

    public static void test() {
        init();
        multiEdgeLogListClients();
        //vectorCLockLogListClients();
        //treeCLockLogListClients();
    }

    static void init() {
        IOUtils.readData();
        edgeId = IOUtils.get("edgeId");
        mqttServerId = IOUtils.get("mqttServerId");
        clientGroupNum = IOUtils.get("clientGroupNum");
        clientGroupId = IOUtils.get("clientGroupId");
        clientNum = IOUtils.get("clientNum");
        throughPut = IOUtils.get("throughPut");

        opNum = throughPut / (clientGroupNum * clientNum);
        threadPool = new ThreadPoolExecutor(clientNum, clientNum, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    private static void showCrdtObject(Random random, CrdtObject object, CountDownLatch latch, int clientId) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TestUtils.randomSleep(random, 1000, 100);    // 为了展现出正确时钟：等待最后一位时钟的回溯
        System.out.println(object.toString());
    }

    static void multiEdgeLogListClients() {
        int clientIdFrom = clientGroupId * clientNum;
        for (int clientId = clientIdFrom; clientId < clientIdFrom + clientNum; clientId++) {
            int finalClientId = clientId;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * clientGroupNum * opNum);
                Semaphore semaphore = new Semaphore(0);
                Factory factory = new MultiEdgeClockFactory(finalClientId, edgeId, clientGroupNum, random, semaphore);
                LogList logList = factory.buildLogList();
                logList.setCountDownLatch(latch);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client: " + finalClientId + " start...");
                TestUtils.randomSleep(random, 2000, 10);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    logList.add(TestUtils.randomStr(random, charRange));
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, logList, latch, finalClientId);
            });
        }
    }

    static void vectorCLockLogListClients() {
        int clientIdFrom = clientGroupId * clientNum;
        for (int clientId = clientIdFrom; clientId < clientIdFrom + clientNum; clientId++) {
            int finalClientId = clientId;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * clientGroupNum * opNum);
                Semaphore semaphore = new Semaphore(0);
                Factory factory = new VectorClockFactory(finalClientId, clientNum * clientGroupNum + 1, edgeId, random, semaphore);
                LogList logList = factory.buildLogList();
                logList.setCountDownLatch(latch);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client: " + finalClientId + " start...");
                TestUtils.randomSleep(random, 2000, 10);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    logList.add(TestUtils.randomStr(random, charRange));
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, logList, latch, finalClientId);
            });
        }
    }

    static void treeCLockLogListClients() {
        int clientIdFrom = clientGroupId * clientNum;
        for (int clientId = clientIdFrom; clientId < clientIdFrom + clientNum; clientId++) {
            int finalClientId = clientId;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * clientGroupNum * opNum);
                Semaphore semaphore = new Semaphore(0);
                Factory factory = new TreeClockFactory(finalClientId, clientNum * clientGroupNum + 1, edgeId, random, semaphore);
                LogList logList = factory.buildLogList();
                logList.setCountDownLatch(latch);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client: " + finalClientId + " start...");
                TestUtils.randomSleep(random, 2000, 10);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    logList.add(TestUtils.randomStr(random, charRange));
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, logList, latch, finalClientId);
            });
        }
    }
}
