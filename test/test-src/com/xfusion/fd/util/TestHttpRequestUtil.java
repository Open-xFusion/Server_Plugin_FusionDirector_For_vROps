package com.xfusion.fd.util;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import com.xfusion.fd.api.TestApiTemplate;
import com.xfusion.fd.api.entity.NodeListEntity;
import com.xfusion.fd.service.bean.FusionDirector;

/**
 * HttpRequestUtil测试类
 *
 * @since 2019-02-18
 */
public class TestHttpRequestUtil extends TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestHttpRequestUtil.class);
    
    @Test
    public void testRequestWithBody() throws Exception {
        FusionDirector fd = getFusionDirector();
        MultiValueMap<String,String> headers = new HttpHeaders();
        headers.add("Authorization", HttpRequestUtil.buildBasicAuthString(fd.getUser(), fd.getCode()));
        String url = "https://{0}:{1}/redfish/v1/rich/Nodes";
        String body = "";
        url = MessageFormat.format(url, fd.getHost(), fd.getPort() + "");
        LOG.info(url);
        NodeListEntity response = HttpRequestUtil.requestWithBody(
                fd.getCertPath(), url, HttpMethod.GET, headers, body, NodeListEntity.class);
        LOG.info(response.getMembers().size());
    }
}
