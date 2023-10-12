package com.xfusion.fd.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;

import com.xfusion.fd.service.bean.FusionDirector;
import com.xfusion.fd.util.TestHttpRequestUtil;

/**
 * api测试类
 *
 * @since 2019-02-18
 */
public abstract class TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestApiTemplate.class);

    private String host;

    private String user;

    private String code;

    private String classifyMethod;
    
    private int port;
    
    private String certPath;

    @Before
    public void setup() throws IOException{
        InputStream in = TestHttpRequestUtil.class.getClassLoader().getResourceAsStream("fusionDirecotor.properties");
        Properties p = new Properties();
        p.load(in);
        host = p.getProperty("fd.host");
        user = p.getProperty("fd.user");
        code = p.getProperty("fd.code");
        certPath = p.getProperty("fd.certPath");
        classifyMethod = p.getProperty("fd.classifyMethod");
        port = Integer.parseInt(p.get("fd.port").toString());
        LOG.debug("{DEBUG} Host for FusionDirector is {}", host);
    }

    /**
     * 获取FD
     *
     * @return FusionDirector
     */
    public FusionDirector getFusionDirector() {
        return new FusionDirector(host, port, user, code, classifyMethod, certPath);
    }
}
