package cn.xu.crdtObject;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.config.Config;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.pojo.operation.OpType;
import cn.xu.pojo.operation.Operation;
import cn.xu.utils.TestUtils;

import java.util.*;

public class LwwMap implements CrdtObject{
    NetLayer netLayer;
    ClockLayer clockLayer;
    private int waitMS; // 转发等待的毫秒: 为 0 时表示不等待

    private ArrayList<Msg> msgList;
    private int sendStart;

    public LwwMap() {
        this(0);
    }

    public LwwMap(int waitMS) {
        this.waitMS = waitMS;
        msgList = new ArrayList<>();
        sendStart = 0;
        // 定时器处理发送消息逻辑
        if (waitMS != 0) {
            Timer timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    synchronized (clockLayer) {
                        for (int i = sendStart; i < msgList.size(); i++) {
                            String msgStr = msgList.get(i).serialized();
                            clockLayer.msgOut(msgStr);
                            System.out.println(msgStr);
                        }
                        sendStart = msgList.size();
                    }
                }
            };
            timer.schedule(task, waitMS, waitMS);
        }
    }

    @Override
    public void setClockLayer(ClockLayer clockLayer) {
        this.clockLayer = clockLayer;
    }

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
    }

    @Override
    public void msgIn(Msg msg) {
        // 消息处理
        dealMsg(msg);
        // 消息记录
        msgList.add(msg);
        // 消息转发
        if (waitMS == 0) {
            String msgStr = msg.serialized();
            clockLayer.msgOut(msgStr);
        }
    }

    @Override
    public void ownMsgIn(Msg msg) {
        // 消息处理
        dealMsg(msg);
        // 消息记录
        msgList.add(msg);
        // 消息转发
        String msgStr = msg.serialized();
        ((MultiEdgeClockLayer) clockLayer).msgOut2Edge(msgStr);
        if (waitMS == 0) {
            clockLayer.msgOut(msgStr);
        }
    }

    @Override
    public void startSyc(String sycEndClientId) {
        for (Msg msg : msgList) {
            netLayer.startSycAsyncSend(msg.serialized());
        }
        netLayer.startSycAsyncSend(sycEndClientId);
        System.out.println("syc success");
    }

    private void dealMsg(Msg msg) {
        Operation op = msg.getOp();
        if (op != null) {
            String msgKey = op.getParams()[0];
            for (Msg hasMsg : msgList) {
                if (hasMsg.getOp() == null) {
                    continue;
                }
                if ((hasMsg.getOp().getParams()[0]).equals(msgKey)) {
                    if (op.getOpType() == OpType.REMOVE) {
                        hasMsg.invalid();
                    } else {
                        if (((MultiEdgeClock)msg.getClock()).totalBigger(hasMsg.getClock())) {
                            hasMsg.invalid();
                        }
                    }
                }
            }
        }
    }
}
