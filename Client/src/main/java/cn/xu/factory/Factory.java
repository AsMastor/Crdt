package cn.xu.factory;

import cn.xu.crdtObject.AwSet;
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
}
