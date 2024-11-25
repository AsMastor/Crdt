package cn.xu;

import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.crdtObject.MvMap;
import cn.xu.factory.Factory;
import cn.xu.factory.SimplifyClockFactory;
import cn.xu.factory.VectorClockFactory;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;
import cn.xu.utils.TestUtils;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientTest {
    static int edgeId = 0;
    static int clientNum = 10;
    static int opNum = 10;
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
                factory = new VectorClockFactory(finalI, clientNum, edgeId, random);
                break;
            case "simplify":
            case "s" :
                factory = new SimplifyClockFactory(finalI, edgeId, random);
                break;
            default:
                throw new RuntimeException("Clock Type Error");
        }
        return factory;
    }

    private void showCrdtObject(CrdtObject object, CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                showCrdtObject(awSet, latch);
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
                showCrdtObject(mvMap, latch);
            });
        }
        TestUtils.foreverSleep();
    }

    @Test
    public void testVectorClockAwSet() {
        testAwSet("v");
    }

    @Test
    public void testVectorClockMvMap() {
        testMvMap("v");
    }

    @Test
    public void testSimplifyClockAwSet() {
        testAwSet("s");
    }

    @Test
    public void testSimplifyClockMvMap() {
        testMvMap("s");
    }

    @Test
    public void testMultiEdgeClock() {
        int nId = 0;
        int eId = 1;
        Random random = new Random();
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseL, random);
        netLayer.asyncSend("test");
    }
}
