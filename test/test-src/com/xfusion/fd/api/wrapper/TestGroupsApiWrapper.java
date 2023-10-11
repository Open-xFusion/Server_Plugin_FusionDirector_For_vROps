package com.xfusion.fd.api.wrapper;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xfusion.fd.api.TestApiTemplate;
import com.xfusion.fd.api.entity.GroupListEntity;
import com.xfusion.fd.api.exception.FusionDirectorException;

/**
 * GroupsApiWrapper测试类
 *
 * @since 2019-02-18
 */
public class TestGroupsApiWrapper extends TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestGroupsApiWrapper.class);
    
    @Test
    public void testCall() throws FusionDirectorException{
        try{
            AbstractApiWrapper wrapper = new GroupListApiWrapper(getFusionDirector());
            GroupListEntity result = wrapper.call(GroupListEntity.class);
            LOG.info(result.getMembers().size());
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }
}
