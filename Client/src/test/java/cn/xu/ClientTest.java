package cn.xu;

import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.crdtObject.MvMap;
import cn.xu.factory.Factory;
import cn.xu.factory.MultiEdgeClockFactory;
import cn.xu.factory.SimplifyClockFactory;
import cn.xu.factory.VectorClockFactory;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;
import cn.xu.utils.TestUtils;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;

public class ClientTest {
    static int edgeId = 0;
    static int clientNum = 15;
    static int opNum = 5;
    static int sleepTime = 10;
    static int sleepTimeRange = 10;
    static int addProbability = 8;
    static int rmvProbability = 2;
    static int charRange = 6;
    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(clientNum, clientNum, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    static Random mainRandom = new Random(System.currentTimeMillis());

    private Factory chooseClockType(String clockType, int finalI, int clientNum, Random random) {
        Factory factory;
        switch (clockType) {
            case "vector" :
            case "v":
                factory = new VectorClockFactory(finalI, clientNum, edgeId, random, null);
                break;
            case "simplify":
            case "s" :
                factory = new SimplifyClockFactory(finalI, edgeId, random, null);
                break;
            default:
                throw new RuntimeException("Clock Type Error");
        }
        return factory;
    }

    private void showCrdtObject(Random random, CrdtObject object, CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TestUtils.randomSleep(random, 500, 100);    // 为了展现出正确时钟：等待最后一位时钟的回溯
        System.out.println(object.toString());
    }

    private void testAwSet(String clockType) {
        for (int i = 0; i < clientNum; i++) {
            int finalI = i;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * opNum);
                Factory factory = chooseClockType(clockType, finalI, clientNum, random);
                AwSet awSet = factory.buildAwSet();
                awSet.setCountDownLatch(latch);
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
                showCrdtObject(random, awSet, latch);
            });
        }
        TestUtils.foreverSleep();
    }

    private void testMvMap(String clockType) {
        for (int i = 0; i < clientNum; i++) {
            int finalI = i;
            threadPool.execute(()->{
                Random random = new Random(mainRandom.nextInt());
                CountDownLatch latch = new CountDownLatch(clientNum * opNum);
                Factory factory = chooseClockType(clockType, finalI, clientNum, random);
                MvMap mvMap = factory.buildMvMap();
                mvMap.setCountDownLatch(latch);
                for (int opIndex = 0; opIndex < opNum; opIndex++) {
                    if (Math.abs(random.nextInt()) % (addProbability + rmvProbability) < addProbability) {
                        mvMap.add(TestUtils.randomStr(random, charRange), TestUtils.randomStr(random, charRange));
                    } else {
                        if (!mvMap.remove(TestUtils.randomStr(random, charRange))) {
                            opIndex--;
                        }
                    }
                    TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                }
                showCrdtObject(random, mvMap, latch);
            });
        }
        TestUtils.foreverSleep();
    }

    //@serverTest
    public void testVectorClockAwSet() {
        testAwSet("v");
    }

    //@serverTest
    public void testVectorClockMvMap() {
        testMvMap("v");
    }

    //@serverTest
    public void testSimplifyClockAwSet() {
        testAwSet("s");
    }

    //@serverTest
    public void testSimplifyClockMvMap() {
        testMvMap("s");
    }

    //@serverTest
    public void testMultiEdgeClock() {
        int nId = 0;
        int eId = 1;
        Random random = new Random();
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, 0, random);
        netLayer.asyncSend("0%0&2&1#2#-1#-1#-1#0#0#0");
    }

    //@serverTest
    public void testMultiEdgeClockMvMap() {
        int edgeServerNum = 3;
        int edgeClientNum = 5;
        int clientId = 0;
        for (int edgeId = 0; edgeId < edgeServerNum; edgeId++) {
            for (int i = 0; i < edgeClientNum; i++) {
                int finalClientId = clientId;
                int finalEdgeId = edgeId;
                threadPool.execute(()->{
                    Random random = new Random(mainRandom.nextInt());
                    CountDownLatch latch = new CountDownLatch(clientNum * opNum);
                    Factory factory = new MultiEdgeClockFactory(finalClientId, finalEdgeId, edgeServerNum, random, new Semaphore(0));
                    MvMap mvMap = factory.buildMvMap();
                    mvMap.setCountDownLatch(latch);
                    TestUtils.randomSleep(random, 2000, 10);
                    for (int opIndex = 0; opIndex < opNum; opIndex++) {
                        if (Math.abs(random.nextInt()) % (addProbability + rmvProbability) < addProbability) {
                            mvMap.add(TestUtils.randomStr(random, charRange), TestUtils.randomStr(random, charRange));
                        } else {
                            if (!mvMap.remove(TestUtils.randomStr(random, charRange))) {
                                opIndex--;
                            }
                        }
                        TestUtils.randomSleep(random, sleepTime, sleepTimeRange);
                    }
                    showCrdtObject(random, mvMap, latch);
                });
                clientId++;
            }
        }
        TestUtils.foreverSleep();
    }
}
