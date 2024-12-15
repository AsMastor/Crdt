package cn.xu.clientTest;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.MvMap;
import cn.xu.factory.Factory;
import cn.xu.factory.SimplifyClockFactory;
import cn.xu.utils.CmdLine;

import java.util.Random;

public class CmdTest {
    static void testAwSet() {
        Factory factory = new SimplifyClockFactory(1, 0, new Random(System.currentTimeMillis()), null);
        AwSet awSet = factory.buildAwSet();
        CmdLine.run(awSet);
    }

    static void testMvMap() {
        Factory factory = new SimplifyClockFactory(1, 0, new Random(System.currentTimeMillis()), null);
        MvMap mvMap = factory.buildMvMap();
        CmdLine.run(mvMap);
    }
}
