package cn.xu.factory;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.crdtObject.MvMap;

public interface Factory {
    /**
     * 负责生成AwSet
     */
    AwSet buildAwSet();

    /**
     * 负责生成MvMap
     */
    MvMap buildMvMap();

    /**
     * 负责生成LwwMap
     */
    LwwMap buildLwwMap();

    LogList buildLogList();
}
