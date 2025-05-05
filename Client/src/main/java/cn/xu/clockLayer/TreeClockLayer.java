package cn.xu.clockLayer;

import cn.xu.backGround.BackGround;
import cn.xu.crdtObject.CrdtObject;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.TreeClock;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class TreeClockLayer implements ClockLayer{
    private final int nid;    // 这个节点的id：取值从 0 —— nodeNum-1
    private final int nodeNum;    // 全局中所有节点的数量
    private final int[] clk;
    private final int[] aClk;
    private final LinkedList<Integer>[] treeStruct; // 这里装的是树节点的子节点
    private final int[] parent;

    private NetLayer netLayer;
    private CrdtObject crdtObject;
    private BackGround backGround;

    public TreeClockLayer(int thisNodeId, int nodeNum) {
        this.nid = thisNodeId;
        this.nodeNum = nodeNum;
        clk = new int[nodeNum];
        aClk = new int[nodeNum];
        treeStruct = new LinkedList[nodeNum];
        for (int i = 0; i < nodeNum; i++) {
            treeStruct[i] = new LinkedList<>();
        }
        parent = new int[nodeNum];
    }

    @Override
    public void setNetLayer(NetLayer netLayer) {
        this.netLayer = netLayer;
    }

    @Override
    public void setCrdtLayer(CrdtObject crdtObject) {
        this.crdtObject = crdtObject;
    }

    @Override
    public void setBackGround(BackGround backGround) {
        this.backGround = backGround;
    }

    @Override
    public void msgIn(Msg msg) {
        // 树时钟是针对 state-based-CRDT，因此不需要因果序的递交消息，所以这里不作因果序排序，直接递交
        synchronized (crdtObject) {
            join((TreeClock) msg.getClock());
            msgSendToCrdt(msg);
        }
    }

    private void msgSendToCrdt(Msg msg) {
        crdtObject.msgIn(msg);
    }

    /**
     * Update with ⊔ joinClock
     */
    private void join(TreeClock joinClock) {
        int joinCLockRoot = joinClock.getRootId();
        if (clk[joinCLockRoot] >= joinClock.getClk()[joinCLockRoot]) {
            return;
        }
        Deque<Integer> s = new ArrayDeque<>();
        getUpdatedNodesJoin(s, joinClock, joinCLockRoot);
        detachNodes(s);
        attachNodes(s, joinClock);
        aClk[joinCLockRoot] = clk[nid];
        pushChild(joinCLockRoot, nid);
    }

    /**
     * Populate S with a pre-order traversal of the subtree rooted at joinClock with nodes whose clock has progressed
     */
    private void getUpdatedNodesJoin(Deque<Integer> s, TreeClock joinClock, int nodeId) {
        for (Integer childId : joinClock.getTreeStruct()[nodeId]) {
            if (clk[childId] < joinClock.getClk()[childId]) {
                getUpdatedNodesJoin(s, joinClock, childId);
            } else {
                break;
            }
        }
        s.push(nodeId);
    }

    /**
     * Detach from T the nodes with tid that appears in S
     */
    private void detachNodes(Deque<Integer> s) {
        for (Integer detachNodeId : s) {
            if (detachNodeId != nid && clk[detachNodeId] != 0) {
                int x = parent[detachNodeId];
                treeStruct[x].removeIf(child -> child.equals(detachNodeId));
            }
        }
    }

    /**
     * Re-attach the nodes of T with tid that appears in S to obtain the shape corresponding to joinClock. T
     */
    private void attachNodes(Deque<Integer> s, TreeClock joinClock) {
        while (!s.isEmpty()) {
            int node = s.pop();
            clk[node] = joinClock.getClk()[node];
            if (node != joinClock.getRootId()) {
                int par = joinClock.getParent()[node];
                aClk[node] = joinClock.getAClk()[node];
                pushChild(node, par);
            }
        }
    }

    /**
     * Push x in the front of head of Chld(y)
     */
    private void pushChild(int x, int y) {
        parent[x] = y;
        treeStruct[y].addFirst(x);
    }

    @Override
    public void msgOut(Msg msg) {
        backGround.siftOutOwnClock(msg.getClock());
        netLayer.asyncSend(msg.serialized());
    }

    @Override
    public Clock generateNextClock() {
        int[] newClk = new int[nodeNum];
        int[] newAClk = new int[nodeNum];
        LinkedList<Integer>[] newTreeStruct = new LinkedList[nodeNum];
        for (int i = 0; i < nodeNum; i++) {
            newTreeStruct[i] = new LinkedList<>();
        }
        int[] newParent = new int[nodeNum];
        clk[nid]++;
        for (int i = 0; i < nodeNum; i++) {
            newClk[i] = clk[i];
            newAClk[i] = aClk[i];
            for (Integer treeNode : treeStruct[i]) {
                newTreeStruct[i].add(treeNode);
            }
            newParent[i] = parent[i];
        }
        return new TreeClock(nid, newClk, newAClk, newTreeStruct, newParent);
    }
}
