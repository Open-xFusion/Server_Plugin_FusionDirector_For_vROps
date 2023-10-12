package com.xfusion.fd.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xfusion.adapter.bean.ResourceKeyCache;
import com.xfusion.fd.service.bean.HealthStatusBean;
import com.xfusion.fd.service.bean.NodeBean;
import com.integrien.alive.common.adapter3.MetricData;
import com.integrien.alive.common.adapter3.ResourceKey;

/**
 * Cache测试类
 *
 * @since 2019-02-18
 */
public class TestCache {
    Logger log = Logger.getLogger(TestCache.class);
    
    @Test
    public void testChangedKeys(){
        NodeBean node = new NodeBean();
        node.setTag("aaaa");
        node.setDeviceID("adfasdfasdfasdf");
        HealthStatusBean health = new HealthStatusBean();
        health.setHealth("OK");
        health.setState("Enabled");
        node.setStatus(health);
        Map<ResourceKey, List<MetricData>> metricsByResource = new HashMap<>();
        ResourceKey key = node.convert2Resource("aa", "test", metricsByResource);
        log.info(ResourceKeyCache.checkKeyLabelChanged(key));
        node.setTag("change...");
        key = node.convert2Resource("aa", "test", metricsByResource);
        log.info(ResourceKeyCache.checkKeyLabelChanged(key));
    }
}
