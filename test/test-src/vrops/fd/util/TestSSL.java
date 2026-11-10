package vrops.fd.util;

import org.apache.log4j.Logger;
import org.junit.Test;

import vrops.fd.api.TestApiTemplate;
import vrops.fd.api.entity.GroupListEntity;
import vrops.fd.api.wrapper.AbstractApiWrapper;
import vrops.fd.api.wrapper.GroupListApiWrapper;

/**
 * SSL测试类
 *
 * @since 2019-02-18
 */
public class TestSSL extends TestApiTemplate {
    Logger log = Logger.getLogger(TestCache.class);

    @Test
    public void test() throws Exception {
        AbstractApiWrapper wrapper = new GroupListApiWrapper(getFusionDirector());
        GroupListEntity result = wrapper.call(GroupListEntity.class);
        log.info(result.getMembers().size());
    }
}
