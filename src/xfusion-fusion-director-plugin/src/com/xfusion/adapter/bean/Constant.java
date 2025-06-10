/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.adapter.bean;

import java.util.Collections;
import java.util.Map;

/**
 * 常量工具类
 *
 * @since 2019-02-18
 */
public class Constant {
    /**
     * 服务器IP
     */
    public static final String KEY_SERVER_IP_ADDRESS = "serverIP";

    /**
     * 服务器地址
     */
    public static final String KEY_SERVER_PORT = "serverPort";

    /**
     * 用户名
     */
    public static final String KEY_FD_ACCOUNT = "username";

    /**
     * FusionDirector 编码
     */
    public static final String KEY_FD_CODE = "fdCode";

    /**
     * 设备分类方式
     */
    public static final String KEY_CLASSIFY_METHOD = "deviceClassificationMethod";

    /**
     * 分组类型
     */
    public static final String CLASSIFY_BY_GROUP = "Group Type";

    /**
     * 型号类型
     */
    public static final String CLASSIFY_BY_MODEL = "Model Type";

    /**
     * 证书地址
     */
    public static final String SSL_CERT_PATH = "sslCertPath";

    /**
     * 收集线程个数
     */
    public static final int MAX_COLLECT_THREAD_COUNT = 5;

    /**
     * 健康状态序列
     */
    public static final Map<String, Integer> HEALTH_STATUS_ORDER = Collections.emptyMap();

    static {
        HEALTH_STATUS_ORDER.put("Unknown", -1);
        HEALTH_STATUS_ORDER.put("OK", 0);
        HEALTH_STATUS_ORDER.put("Warning", 1);
        HEALTH_STATUS_ORDER.put("Immediate", 2);
        HEALTH_STATUS_ORDER.put("Critical", 3);
    }
}
