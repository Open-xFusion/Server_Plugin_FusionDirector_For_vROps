package vrops.fd.api.wrapper;

import org.apache.log4j.Logger;
import org.junit.Test;


import vrops.fd.api.entity.GroupListEntity;
import vrops.fd.api.exception.FusionDirectorException;
import vrops.fd.api.TestApiTemplate;

/**
 * GroupsApiWrapper测试类
 *
 * @since 2019-02-18
 */
public class TestGroupsApiWrapper extends TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestGroupsApiWrapper.class);

    @Test
    public void testCall() throws FusionDirectorException {
        try {
            AbstractApiWrapper wrapper = new GroupListApiWrapper(getFusionDirector());
            GroupListEntity result = wrapper.call(GroupListEntity.class);
            LOG.info(result.getMembers().size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }
}
