package vrops.fd.api.wrapper;

import java.text.MessageFormat;


import org.apache.log4j.Logger;
import org.junit.Test;

import vrops.fd.api.TestApiTemplate;
import vrops.fd.api.entity.DriveEntity;
import vrops.fd.api.entity.DriveListEntity;
import vrops.fd.api.entity.NodeEntity;
import vrops.fd.api.entity.NodeListEntity;
import vrops.fd.api.entity.ProcessorListEntity;
import vrops.fd.api.entity.RaidCardEntity;
import vrops.fd.api.entity.RaidCardListEntity;
import vrops.fd.api.entity.VolumeEntity;
import vrops.fd.api.entity.VolumeListEntity;
import vrops.fd.api.exception.FusionDirectorException;
import vrops.fd.service.bean.DriveBean;
import vrops.fd.service.bean.FusionDirector;
import vrops.fd.service.bean.NodeBean;
import vrops.fd.service.bean.RaidCardBean;
import vrops.fd.service.bean.VolumeBean;

/**
 * NodesApiWrapper测试类
 *
 * @since 2019-02-18
 */
public class TestNodesApiWrapper extends TestApiTemplate {
    private static final Logger LOG = Logger.getLogger(TestNodesApiWrapper.class);

    @Test
    public void testCall() throws FusionDirectorException {
        try {
            FusionDirector fd = getFusionDirector();
            AbstractApiWrapper wrapper = new NodeListApiWrapper(fd);
            NodeListEntity result = wrapper.call(NodeListEntity.class);
            LOG.info("{} nodes found.", result.getMembers().size());
            for (NodeEntity node : result.getMembers()) {
                AbstractApiWrapper nodeApiWrapper = new NodeApiWrapper(fd);
                nodeApiWrapper.setPathVarivable(node.getDeviceID());

                NodeBean nodeBean = nodeApiWrapper.call(NodeBean.class);
                LOG.info(MessageFormat.format("node with id = [{0}] SerialNumber = [{1}]",
                        node.getDeviceID(), nodeBean.getSerialNumber()));

                // processor
                AbstractApiWrapper processApiWrapper = new ProcessorListApiWrapper(fd);
                processApiWrapper.setPathVarivable(node.getDeviceID());
                ProcessorListEntity processorListEntity = processApiWrapper.call(ProcessorListEntity.class);
                LOG.info("{} processors found", processorListEntity.getMembers().size());

                // drive
                AbstractApiWrapper driveListApiWrapper = new DriveListApiWrapper(fd);
                driveListApiWrapper.setPathVarivable(node.getDeviceID());
                DriveListEntity driveListEntity = driveListApiWrapper.call(DriveListEntity.class);
                LOG.info("{} drives found", driveListEntity.getMembers().size());
                for (DriveEntity entity : driveListEntity.getMembers()) {
                    AbstractApiWrapper driveApiWrapper = new DriveApiWrapper(fd);
                    driveApiWrapper.setPathVarivable(node.getDeviceID(), entity.getDeviceID());
                    DriveBean driveBean = driveApiWrapper.call(DriveBean.class);
                    LOG.info("Drive device id = {}", driveBean.getDeviceID());
                }

                // raid card
                testRaidCard(node);
            }
            LOG.info("Unit test PASSED!");
        } catch (Exception e) {
            LOG.error("Unit test FAILED!", e);
            throw e;
        }
    }

    private void testRaidCard(NodeEntity node) {
        AbstractApiWrapper raidCardListApiWrapper = new RaidCardListApiWrapper(fd);
        raidCardListApiWrapper.setPathVarivable(node.getDeviceID());
        RaidCardListEntity raidCardListEntity = raidCardListApiWrapper.call(RaidCardListEntity.class);
        LOG.info("{} raid card found", raidCardListEntity.getMembers().size());
        for (RaidCardEntity entity : raidCardListEntity.getMembers()) {
            AbstractApiWrapper raidCardApiWrapper = new RaidCardApiWrapper(fd);
            raidCardApiWrapper.setPathVarivable(node.getDeviceID(), entity.getDeviceID());
            RaidCardBean raidCardBean = raidCardApiWrapper.call(RaidCardBean.class);
            LOG.info("Raid card device id = {}", raidCardBean.getDeviceID());

            // volume
            AbstractApiWrapper volumeListApiWrapper = new VolumeListApiWrapper(fd);
            volumeListApiWrapper.setPathVarivable(node.getDeviceID(), entity.getDeviceID());
            VolumeListEntity volumeListEntity = volumeListApiWrapper.call(VolumeListEntity.class);
            LOG.info("{} volumes found", volumeListEntity.getMembers().size());
            for (VolumeEntity volumeEntity : volumeListEntity.getMembers()) {
                AbstractApiWrapper volumeApiWrapper = new VolumeApiWrapper(fd);
                volumeApiWrapper.setPathVarivable(node.getDeviceID(), entity.getDeviceID(),
                        volumeEntity.getDeviceID());
                VolumeBean volumeBean = volumeApiWrapper.call(VolumeBean.class);
                LOG.info("Volume device id = {}", volumeBean.getDeviceID());
            }
        }
    }
}
