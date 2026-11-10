/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.service;

import vrops.adapter.bean.Constant;
import vrops.fd.api.entity.EnclosureEntity;
import vrops.fd.api.entity.EnclosureListEntity;
import vrops.fd.api.entity.FusionDirectorVersionEntity;
import vrops.fd.api.entity.GroupEntity;
import vrops.fd.api.entity.GroupListEntity;
import vrops.fd.api.entity.NodeEntity;
import vrops.fd.api.entity.SwitchNodeEntity;
import vrops.fd.api.entity.SwitchNodeListEntity;
import vrops.fd.api.exception.FusionDirectorException;
import vrops.fd.api.wrapper.AbstractApiWrapper;
import vrops.fd.api.wrapper.EnclosureApiWrapper;
import vrops.fd.api.wrapper.EnclosureListApiWrapper;
import vrops.fd.api.wrapper.FusionDirectorVersionWrapper;
import vrops.fd.api.wrapper.GroupApiWrapper;
import vrops.fd.api.wrapper.GroupListApiWrapper;
import vrops.fd.api.wrapper.SwitchNodeApiWrapper;
import vrops.fd.api.wrapper.SwitchNodeListApiWrapper;
import vrops.fd.service.bean.EnclosureBean;
import vrops.fd.service.bean.FusionDirector;
import vrops.fd.service.bean.GroupBean;
import vrops.fd.service.bean.GroupResourceBean;
import vrops.fd.service.bean.NodeBean;
import vrops.fd.service.bean.SlotBean;
import vrops.fd.service.bean.SwitchNodeBean;
import vrops.fd.util.ConvertUtil;

import com.integrien.alive.common.adapter3.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FusionDirectorServiceImpl
 *
 * @since 2019-02-18
 */
public class FusionDirectorServiceImpl implements FusionDirectorService {
    private FusionDirector fusionDirector;

    private final Logger logger;

    private Set<String> modelSet = new HashSet<String>();

    private Set<String> groupSet = new HashSet<String>();

    private List<NodeBean> nodeList = new ArrayList<>();

    private List<GroupBean> groupList = new ArrayList<>();

    private List<SwitchNodeBean> switchNodeList = new ArrayList<>();

    private List<EnclosureBean> enclosureList = new ArrayList<>();

    private Map<String, String> nodeHealthMap = new HashMap<>();

    private Map<String, String> switchNodeHealthMap = new HashMap<>();

    private Map<String, NodeBean> bladeNodeMap = new HashMap<>();

    private String version;

    /**
     * 实现类接口
     *
     * @param fd FD对象
     * @param log 日志记录对象
     * @return BladeNode 节点信息
     */
    public FusionDirectorServiceImpl(FusionDirector fd, Logger log) {
        fusionDirector = fd;
        this.logger = log;
        long startTime = System.currentTimeMillis();
        try {
            collectVersion();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            collectGroup();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            collectNodes();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            collectEnclosure();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            collectSwitchNode();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        long elpasedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        logger.info(
                "Collected all resources from "
                        + fusionDirector.getHost()
                        + ", elapsed time: "
                        + elpasedSeconds
                        + " seconds");

        processEnclosreIndex();
    }

    @Override
    public NodeBean getBladeNode(String deviceId) {
        return bladeNodeMap.get(deviceId);
    }

    @Override
    public List<NodeBean> getAllNodes() {
        return nodeList;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    private void collectVersion() {
        AbstractApiWrapper wrapper = new FusionDirectorVersionWrapper(fusionDirector);
        FusionDirectorVersionEntity result = null;
        try {
            result = wrapper.call(FusionDirectorVersionEntity.class);
            this.version = result.getCurrentVersion();
        } catch (FusionDirectorException e) {
            isOffLineException(e);
            logger.error(e.getMessage(), e);
        }
    }

    private void collectNodes() {
        ResourceCollector<NodeEntity, NodeBean> collector = new NodeCollector(this.logger, this.fusionDirector);
        collector.initTaskList();
        collector.collect();
        this.nodeList = collector.getCollectResult();
    }

    private void setSwitchNodeIndex(String deviceId, SlotBean slot) {
        for (SwitchNodeBean switchNode : switchNodeList) {
            if (switchNode.getDeviceID().equals(deviceId)) {
                switchNode.setSlotBean(slot);
                return;
            }
        }
    }

    @Override
    public List<GroupBean> getAllNodeGroup() {
        return this.groupList;
    }

    private void collectGroup() {
        AbstractApiWrapper wrapper = new GroupListApiWrapper(fusionDirector);

        GroupListEntity result = new GroupListEntity();
        try {
            result = wrapper.call(GroupListEntity.class);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        for (GroupEntity group : result.getMembers()) {
            AbstractApiWrapper groupApiWrapper = new GroupApiWrapper(fusionDirector);
            groupApiWrapper.setPathVariable(Collections.singletonList(String.valueOf(group.getId())));
            try {
                groupList.add(groupApiWrapper.call(GroupBean.class));
            } catch (FusionDirectorException e) {
                isOffLineException(e);
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<SwitchNodeBean> getAllSwitchNode() {
        return this.switchNodeList;
    }

    private void collectSwitchNode() {
        AbstractApiWrapper wrapper = new SwitchNodeListApiWrapper(fusionDirector);
        List<SwitchNodeEntity> results = null;
        try {
            results = wrapper.callList(SwitchNodeListEntity.class);
        } catch (FusionDirectorException e) {
            isOffLineException(e);
            logger.error(e.getMessage(), e);
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        for (SwitchNodeEntity entity : results) {
            AbstractApiWrapper switchNodeApiWrapper = new SwitchNodeApiWrapper(fusionDirector);
            switchNodeApiWrapper.setPathVariable(Collections.singletonList(entity.getDeviceID()));
            try {
                SwitchNodeBean switchNodeBean = switchNodeApiWrapper.call(SwitchNodeBean.class);

                switchNodeList.add(switchNodeBean);
                switchNodeHealthMap.put(switchNodeBean.getDeviceID(), switchNodeBean.getHealth());
            } catch (FusionDirectorException e) {
                isOffLineException(e);
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<EnclosureBean> getAllEnclosure() {
        return this.enclosureList;
    }

    private void collectEnclosure() {
        AbstractApiWrapper enclosureListApiWrapper = new EnclosureListApiWrapper(fusionDirector);
        List<EnclosureEntity> results = null;
        try {
            results = enclosureListApiWrapper.callList(EnclosureListEntity.class);
        } catch (FusionDirectorException e) {
            isOffLineException(e);
            logger.error(e.getMessage(), e);
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }

        for (EnclosureEntity entity : results) {
            AbstractApiWrapper enclosureApiWrapper = new EnclosureApiWrapper(fusionDirector);
            enclosureApiWrapper.setPathVariable(Collections.singletonList(entity.getDeviceID()));
            try {
                EnclosureBean enclosureBean = enclosureApiWrapper.call(EnclosureBean.class);
                enclosureBean.setDeviceID(entity.getDeviceID());
                enclosureList.add(enclosureBean);
            } catch (FusionDirectorException e) {
                isOffLineException(e);
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<GroupResourceBean> getClassifyGroup() {
        String resourceName = "classifyGroup";

        List<GroupResourceBean> classifyList = new ArrayList<>();
        if (Constant.CLASSIFY_BY_GROUP.equals(fusionDirector.getClassifyMethod())) {
            for (String modelType : groupSet) {
                classifyList.add(new GroupResourceBean(resourceName, modelType));
            }
        } else {
            // classify method is "Model Type"
            for (String modelType : modelSet) {
                classifyList.add(new GroupResourceBean(resourceName, modelType));
            }
        }
        return classifyList;
    }

    @Override
    public String getNodeHealth(String deviceId) {
        return this.nodeHealthMap.get(deviceId);
    }

    @Override
    public String getSwitchNodeHealth(String deviceId) {
        return this.switchNodeHealthMap.get(deviceId);
    }

    private void isOffLineException(Exception e) {
        fusionDirector.isOffLineException(e);
    }

    private void processEnclosreIndex() {
        Map<String, NodeBean> nodeMap = new HashMap<>();
        for (NodeBean node : nodeList) {
            nodeMap.put(node.getDeviceID(), node);
        }
        for (EnclosureBean enclosure : enclosureList) {
            getServerSlot(nodeMap, enclosure);
            getSwitchSlot(enclosure);
        }
        for (NodeBean nodeBean : nodeList) {
            nodeHealthMap.put(nodeBean.getDeviceID(), nodeBean.getHealth());
            if (nodeBean.getModel() != null && nodeBean.getModel().length() > 0) {
                modelSet.add(nodeBean.getModel());
            }
            for (String group : nodeBean.getGroup()) {
                groupSet.add(group);
            }
        }
    }

    private void getSwitchSlot(EnclosureBean enclosure) {
        for (SlotBean slot : enclosure.getSwitchSlots()) {
            if (slot.getDeviceID() != null) {
                setSwitchNodeIndex(slot.getDeviceID(), slot);
            }
        }
    }

    private void getServerSlot(Map<String, NodeBean> nodeMap, EnclosureBean enclosure) {
        for (SlotBean slot : enclosure.getServerSlots()) {
            if (slot.getDeviceID() != null) {
                NodeBean node = nodeMap.get(slot.getDeviceID());
                setBladeNodeMap(slot, node);
            }
        }
    }

    private void setBladeNodeMap(SlotBean slot, NodeBean node) {
        if (node != null) {
            try {
                NodeBean bladeNode = ConvertUtil.deepClone(node);
                bladeNode.setSlotBean(slot);
                bladeNodeMap.put(bladeNode.getDeviceID(), bladeNode);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
