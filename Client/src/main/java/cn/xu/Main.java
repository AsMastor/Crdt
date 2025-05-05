package cn.xu;

import cn.xu.clientTest.*;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.pojo.clock.TreeClock;
import cn.xu.pojo.clock.VevtorClock;
import cn.xu.pojo.operation.OpType;
import cn.xu.pojo.operation.Operation;
import com.alibaba.fastjson.JSON;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        int nodeId = Integer.parseInt(args[0]);     // 在 FunctionTest 和 TimeTest 中表示 groupId
        int eId = Integer.parseInt(args[1]);
        int edgeNum = Integer.parseInt(args[2]);

        //CmdTest.testMultiEdgeLwwMap(nodeId, eId, edgeNum);
        //CmdTest.testMultiEdgeAwSet(nodeId, eId, edgeNum);
        //CmdTest.testMultiEdgeLogList(nodeId, eId, edgeNum);

        FunctionTest.test(nodeId, eId, edgeNum);
        //TimeTest.test(nodeId, eId, edgeNum);
        //RamTest.test();
        //BrandwidthTest.test();
    }
}