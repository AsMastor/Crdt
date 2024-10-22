package cn.xu;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.MvMap;
import cn.xu.factory.Factory;
import cn.xu.factory.SimplifyClockFactory;
import cn.xu.utils.CmdLine;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        testAwSet();
    }

    static void testAwSet() {
        Factory factory = new SimplifyClockFactory(1, new Random(System.currentTimeMillis()));
        AwSet awSet = factory.buildAwSet();
        CmdLine.run(awSet);
    }

    static void testMvMap() {
        Factory factory = new SimplifyClockFactory(1, new Random(System.currentTimeMillis()));
        MvMap mvMap = factory.buildMvMap();
        CmdLine.run(mvMap);
    }
}