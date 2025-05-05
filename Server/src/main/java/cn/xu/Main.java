package cn.xu;

import cn.xu.serverTest.ServerTest;

public class Main {
    public static void main(String[] args) {
        int edgeId = Integer.parseInt(args[0]);
        int edgeNum = Integer.parseInt(args[1]);

        //ServerTest.testVector(edgeId, edgeNum);
        ServerTest.testMultiEdge(edgeId, edgeNum);
    }
}