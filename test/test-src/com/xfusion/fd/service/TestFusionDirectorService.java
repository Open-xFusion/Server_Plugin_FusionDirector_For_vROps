package com.xfusion.fd.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xfusion.fd.api.TestApiTemplate;
import com.xfusion.fd.service.bean.EnclosureBean;
import com.xfusion.fd.service.bean.FusionDirector;
import com.xfusion.fd.service.bean.NodeBean;
import com.xfusion.fd.service.bean.SwitchNodeBean;

/**
 * FusionDirectorService测试类
 *
 * @since 2019-02-18
 */
public class TestFusionDirectorService extends TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestFusionDirectorService.class);
    
    @Test
    public void testAll() {
        LOG.info("FD host is:{}", getFusionDirector().getHost());
        
        long start = System.currentTimeMillis();
        
        FusionDirector fd = getFusionDirector();
        
        FusionDirectorService service = new FusionDirectorServiceImpl(fd, LOG);
        List<NodeBean> nodeList = service.getAllNodes();
        long seconds = (System.currentTimeMillis() - start) / 1000; 
        LOG.info("Elapsed time for resource collection is: {} seconds.", seconds);
        LOG.info("FusionDirector state is: {}", fd.getState());
        
        LOG.info("testGetAllNodes(): {} nodes found.", nodeList.size());
        
        LOG.info("testGetAllNodeGroup(): {} groups found.", service.getAllNodeGroup().size());
        
        List<SwitchNodeBean> list = service.getAllSwitchNode();
        LOG.info("testGetAllSwitchNode(): {} switch nodes found.", list.size());
        
        List<EnclosureBean> enclosureList = service.getAllEnclosure();        
        LOG.info("testGetAllEnclosure(): {} enclosures found.", enclosureList.size());
        for (EnclosureBean bean : enclosureList) {
            LOG.info("Enclosure with device id = {} link to node {}, {}", bean.getDeviceID(), bean.getLinkedNodeIds());
            LOG.info("Enclosure with device id = {} link to switch node, {}",
                    bean.getDeviceID(), bean.getLinkedSwitchNodeIds());
        }
        
        LOG.info("FusionDirector version is: {}", service.getVersion());
        
        LOG.info("classify group size is: {}", service.getClassifyGroup().size());
    }
}
