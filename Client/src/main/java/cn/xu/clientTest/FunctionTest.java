package cn.xu.clientTest;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.factory.Factory;
import cn.xu.factory.MultiEdgeClockFactory;
import cn.xu.utils.IOUtils;
import cn.xu.utils.TestUtils;

import java.util.Random;
import java.util.concurrent.*;

public class FunctionTest {
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
    static int sleepTime = 1000;    // 一个客户端在每个操作间隙的停顿时间
    // 概率设置
    static int sleepTimeRange = 0;
    static int addProbability = 8;
    static int rmvProbability = 2;
    static int charRange = 26;
    // random 和 线程池
    static Random mainRandom = new Random(System.currentTimeMillis());
    static ThreadPoolExecutor threadPool;

    public static void test(int groupId, int eId, int edgeNum) {
        //init();
        testInit(groupId, eId, edgeNum);

        multiEdgeLwwMapClients();
        //multiEdgeAwSetClients();
        //multiEdgeLogListClients();
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

    static void testInit(int groupId, int eId, int edgeNum) {
        edgeId = eId;
        mqttServerId = eId;
        clientGroupNum = edgeNum;
        clientGroupId = groupId;
        clientNum = 4;      // 一个客户端组中，有 N 个客户端
        throughPut = 40;  // 总的吞吐量为 X

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

    static void multiEdgeLwwMapClients() {
        int clientIdFrom = clientGroupId * clientNum;
        for (int clientId = clientIdFrom; clientId < clientIdFrom + clientNum; clientId++) {
            int finalClientId = clientId;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * clientGroupNum * opNum);
                Semaphore semaphore = new Semaphore(0);
                Factory factory = new MultiEdgeClockFactory(finalClientId, edgeId, clientGroupNum, random, semaphore);
                LwwMap lwwMap = factory.buildLwwMap();
                lwwMap.setCountDownLatch(latch);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client: " + finalClientId + " start...");
                TestUtils.randomSleep(random, 2000, 10);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    if (Math.abs(random.nextInt()) % (addProbability + rmvProbability) < addProbability) {
                        lwwMap.add(TestUtils.randomStr(random, charRange), TestUtils.randomStr(random, charRange));
                    } else {
                        if (!lwwMap.remove(TestUtils.randomStr(random, charRange))) {
                            opIndex--;
                        }
                    }
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, lwwMap, latch, finalClientId);
            });
        }
    }

    static void multiEdgeAwSetClients() {
        int clientIdFrom = clientGroupId * clientNum;
        for (int clientId = clientIdFrom; clientId < clientIdFrom + clientNum; clientId++) {
            int finalClientId = clientId;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * clientGroupNum * opNum);
                Semaphore semaphore = new Semaphore(0);
                Factory factory = new MultiEdgeClockFactory(finalClientId, edgeId, clientGroupNum, random, semaphore);
                AwSet awSet = factory.buildAwSet();
                awSet.setCountDownLatch(latch);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client: " + finalClientId + " start...");
                TestUtils.randomSleep(random, 2000, 10);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    if (Math.abs(random.nextInt()) % (addProbability + rmvProbability) < addProbability) {
                        awSet.add(TestUtils.randomStr(random, charRange));
                    } else {
                        if (!awSet.remove(TestUtils.randomStr(random, charRange))) {
                            opIndex--;
                        }
                    }
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, awSet, latch, finalClientId);
            });
        }
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
                    if (Math.abs(random.nextInt()) % (addProbability + rmvProbability) < addProbability) {
                        logList.add(TestUtils.randomStr(random, charRange));
                    } else {
                        if (!logList.removeRecent()) {
                            opIndex--;
                        }
                    }
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, logList, latch, finalClientId);
            });
        }
    }
}
