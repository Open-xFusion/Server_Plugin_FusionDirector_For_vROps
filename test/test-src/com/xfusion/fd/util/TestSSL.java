package com.xfusion.fd.util;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xfusion.fd.api.TestApiTemplate;
import com.xfusion.fd.api.entity.GroupListEntity;
import com.xfusion.fd.api.wrapper.AbstractApiWrapper;
import com.xfusion.fd.api.wrapper.GroupListApiWrapper;

/**
 * SSL测试类
 *
 * @since 2019-02-18
 */
public class TestSSL extends TestApiTemplate {
    Logger log = Logger.getLogger(TestCache.class);
    
    @Test
    public void test() throws Exception{
        AbstractApiWrapper wrapper = new GroupListApiWrapper(getFusionDirector());
        GroupListEntity result = wrapper.call(GroupListEntity.class);
        log.info(result.getMembers().size());
    }
}
