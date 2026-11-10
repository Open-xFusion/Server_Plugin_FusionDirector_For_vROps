/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.service;

import vrops.adapter.bean.Constant;
import vrops.fd.api.entity.CatalogueEntity;
import vrops.fd.api.entity.DriveEntity;
import vrops.fd.api.entity.DriveListEntity;
import vrops.fd.api.entity.FanListEntity;
import vrops.fd.api.entity.MemoryListEntity;
import vrops.fd.api.entity.NetworkAdapterEntity;
import vrops.fd.api.entity.NetworkAdapterListEntity;
import vrops.fd.api.entity.NodeEntity;
import vrops.fd.api.entity.NodeListEntity;
import vrops.fd.api.entity.NodeStatisticsEntity;
import vrops.fd.api.entity.PCIEEntity;
import vrops.fd.api.entity.PCIEListEntity;
import vrops.fd.api.entity.PowerListEntity;
import vrops.fd.api.entity.ProcessorListEntity;
import vrops.fd.api.entity.RaidCardEntity;
import vrops.fd.api.entity.RaidCardListEntity;
import vrops.fd.api.exception.FusionDirectorException;
import vrops.fd.api.wrapper.AbstractApiWrapper;
import vrops.fd.api.wrapper.CatalogueApiWrapper;
import vrops.fd.api.wrapper.DriveApiWrapper;
import vrops.fd.api.wrapper.DriveListApiWrapper;
import vrops.fd.api.wrapper.FanListApiWrapper;
import vrops.fd.api.wrapper.MemoryListApiWrapper;
import vrops.fd.api.wrapper.NetworkAdapterApiWrapper;
import vrops.fd.api.wrapper.NetworkAdapterListApiWrapper;
import vrops.fd.api.wrapper.NodeApiWrapper;
import vrops.fd.api.wrapper.NodeListApiWrapper;
import vrops.fd.api.wrapper.NodeStatisticsApiWrapper;
import vrops.fd.api.wrapper.PCIEApiWrapper;
import vrops.fd.api.wrapper.PCIEListApiWrapper;
import vrops.fd.api.wrapper.PowerListApiWrapper;
import vrops.fd.api.wrapper.ProcessorListApiWrapper;
import vrops.fd.api.wrapper.RaidCardApiWrapper;
import vrops.fd.api.wrapper.RaidCardListApiWrapper;
import vrops.fd.service.bean.DriveBean;
import vrops.fd.service.bean.FusionDirector;
import vrops.fd.service.bean.GroupResourceBean;
import vrops.fd.service.bean.NetworkAdapterBean;
import vrops.fd.service.bean.NodeBean;
import vrops.fd.service.bean.PCIEBean;
import vrops.fd.service.bean.RaidCardBean;
import vrops.fd.service.bean.StorageControllerBean;
import vrops.fd.util.HealthToolkit;
import vrops.fd.service.bean.ProcessorBean;
import vrops.fd.service.bean.MemoryBean;

import com.integrien.alive.common.adapter3.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * NodeCollector
 *
 * @since 2019-02-18
 */
public class NodeCollector implements ResourceCollector<NodeEntity, NodeBean>, Runnable {
    private static Logger logger = null;

    private List<NodeEntity> taskList = new ArrayList<>();

    private List<NodeBean> resultList = new ArrayList<>();

    private FusionDirector fusionDirector;

    private CountDownLatch countDownLatch = new CountDownLatch(Constant.MAX_COLLECT_THREAD_COUNT);

    private int index = 0;


    public NodeCollector(Logger logger, FusionDirector fusionDirector) {
        this.fusionDirector = fusionDirector;
        this.logger = logger;
    }

    @Override
    public void initTaskList() {
        AbstractApiWrapper wrapper = new NodeListApiWrapper(fusionDirector);
        try {
            taskList = wrapper.callList(NodeListEntity.class);
            resultList = new ArrayList<>(taskList.size());
        } catch (FusionDirectorException e) {
            fusionDirector.isOffLineException(e);
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void collect() {
        if (this.taskList == null || this.taskList.isEmpty()) {
            return;
        }

        for (int i = 0; i < Constant.MAX_COLLECT_THREAD_COUNT; i++) {
            this.startCollectThreat(i);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public List<NodeBean> getCollectResult() {
        return this.resultList;
    }

    @Override
    public synchronized void onResouceCollected(NodeBean t) {
        if (t != null) {
            resultList.add(t);
        }
    }

    @Override
    public void finish() {
        countDownLatch.countDown();
    }

    @Override
    public synchronized NodeEntity getTask() {
        NodeEntity task = null;

        if ("offline".equals(this.fusionDirector.getState())) {
            taskList.clear();
            return null;
        }

        if (this.index < this.taskList.size()) {
            task = this.taskList.get(index);
            this.index++;
        }
        return task;
    }

    /**
     * 执行方法
     */
    public void run() {
        while (true) {
            NodeEntity taskNode = this.getTask();
            if (taskNode == null) {
                break;
            }
            NodeBean nodeBean = getNodeBean(taskNode);
            if (nodeBean == null) {
                continue;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("InterruptedException Exception", e);
            }
            CatalogueEntity catalog = getCatalogueEntity(taskNode, nodeBean);
            setNodeStatisticsData(taskNode, nodeBean);
            // Processor
            setNodeProcessor(taskNode, nodeBean);
            // drive
            GroupResourceBean storageGroup = new GroupResourceBean("storageGroup", "Storage Group");
            setNodeDrives(taskNode, nodeBean, catalog, storageGroup);
            // Raid card
            setNodeRaidCard(taskNode, storageGroup);
            // Memory
            setNodeMemory(taskNode, nodeBean);
            // PCIE
            setNodePCIE(taskNode, nodeBean);
            // Power
            setNodePower(taskNode, nodeBean, catalog);
            // Thermal
            setNodeThermal(taskNode, nodeBean, catalog);
            // NetworkAdapter
            setNodeNetworkAdapter(taskNode, nodeBean);
            onResouceCollected(nodeBean);
        }
        finish();
    }

    private void setNodeNetworkAdapter(NodeEntity taskNode, NodeBean nodeBean) {
        AbstractApiWrapper networkAdapterListApiWrapper = new NetworkAdapterListApiWrapper(fusionDirector);
        networkAdapterListApiWrapper.setPathVariable(Collections.singletonList(taskNode.getDeviceID()));
        NetworkAdapterListEntity networkAdapterListEntity;
        try {
            networkAdapterListEntity = networkAdapterListApiWrapper.call(NetworkAdapterListEntity.class);
            GroupResourceBean networkAdapterGroup =
                    new GroupResourceBean("networkAdapterGroup", "Network Adapter Group");
            HealthToolkit networkAdapterGroupHealthToolkit = new HealthToolkit();
            for (NetworkAdapterEntity entity : networkAdapterListEntity.getMembers()) {
                AbstractApiWrapper networkAdapterApiWrapper = new NetworkAdapterApiWrapper(fusionDirector);
                networkAdapterApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID(), entity.getDeviceID()));

                NetworkAdapterBean networkAdapterBean = networkAdapterApiWrapper.call(NetworkAdapterBean.class);
                networkAdapterGroup.addChild(networkAdapterBean);
                networkAdapterGroupHealthToolkit.pushHealth(networkAdapterBean.getStatus().getHealth());
            }
            networkAdapterGroup.setHealthStatus(networkAdapterGroupHealthToolkit.getHealth());
            nodeBean.addChild(networkAdapterGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodeThermal(NodeEntity taskNode, NodeBean nodeBean, CatalogueEntity catalog) {
        AbstractApiWrapper fanApiWrapper = new FanListApiWrapper(fusionDirector);
        fanApiWrapper.setPathVariable(Collections.singletonList(taskNode.getDeviceID()));

        FanListEntity fanListEntity;
        try {
            fanListEntity = fanApiWrapper.call(FanListEntity.class);

            GroupResourceBean fanGroup = new GroupResourceBean("fanGroup", "Fan Group");
            if (catalog != null) {
                fanGroup.setHealthStatus(catalog.getFanHealth());
            }

            fanGroup.addChildren(fanListEntity.getMembers());
            nodeBean.addChild(fanGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodePower(NodeEntity taskNode, NodeBean nodeBean, CatalogueEntity catalog) {
        AbstractApiWrapper powerApiWrapper = new PowerListApiWrapper(fusionDirector);
        powerApiWrapper.setPathVariable(Collections.singletonList(taskNode.getDeviceID()));
        PowerListEntity powerListEntity;
        try {
            powerListEntity = powerApiWrapper.call(PowerListEntity.class);
            GroupResourceBean powerGroup = new GroupResourceBean("powerGroup", "Power Group");
            if (catalog != null) {
                powerGroup.setHealthStatus(catalog.getPowerHealth());
            }
            powerGroup.addChildren(powerListEntity.getMembers());
            nodeBean.addChild(powerGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodePCIE(NodeEntity taskNode, NodeBean nodeBean) {
        AbstractApiWrapper pcieListApiWrapper = new PCIEListApiWrapper(fusionDirector);
        pcieListApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        PCIEListEntity pcieListEntity;
        try {
            pcieListEntity = pcieListApiWrapper.call(PCIEListEntity.class);
            GroupResourceBean pcieGroup = new GroupResourceBean("pcieGroup", "PCIe Card Group");
            HealthToolkit pcieGroupHealthToolkit = new HealthToolkit();
            for (PCIEEntity entity : pcieListEntity.getMembers()) {
                if (entity.getName() != null && entity.getName().contains("OCP")) {
                    // 过滤OCP卡
                    continue;
                }
                AbstractApiWrapper pcieApiWrapper = new PCIEApiWrapper(fusionDirector);
                pcieApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID(), entity.getDeviceID()));
                PCIEBean pcieBean = pcieApiWrapper.call(PCIEBean.class);
                pcieGroupHealthToolkit.pushHealth(pcieBean.getStatus().getHealth());
                pcieGroup.addChild(pcieBean);
            }
            pcieGroup.setHealthStatus(pcieGroupHealthToolkit.getHealth());
            nodeBean.addChild(pcieGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodeMemory(NodeEntity taskNode, NodeBean nodeBean) {
        AbstractApiWrapper memoryListApiWrapper = new MemoryListApiWrapper(fusionDirector);
        memoryListApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        MemoryListEntity memoryListEntity;
        try {
            memoryListEntity = memoryListApiWrapper.call(MemoryListEntity.class);
            GroupResourceBean memoryGroup = new GroupResourceBean("memoryGroup", "Memory Group");
            List<MemoryBean> memoryBeanList = memoryListEntity.getMemoryBeans();
            HealthToolkit memoryGroupHealthToolkit = new HealthToolkit();
            for (MemoryBean memoryBean : memoryBeanList) {
                memoryGroupHealthToolkit.pushHealth(memoryBean.getStatus().getHealth());
            }
            memoryGroup.setHealthStatus(memoryGroupHealthToolkit.getHealth());
            memoryGroup.addChildren(memoryListEntity.getMembers());
            nodeBean.addChild(memoryGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodeRaidCard(NodeEntity taskNode, GroupResourceBean storageGroup) {
        RaidCardListEntity raidCardListEntity;
        AbstractApiWrapper raidCardListApiWrapper = new RaidCardListApiWrapper(fusionDirector);
        raidCardListApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        try {
            raidCardListEntity = raidCardListApiWrapper.call(RaidCardListEntity.class);
            processRaidCard(taskNode, storageGroup, raidCardListEntity);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void processRaidCard(
            NodeEntity taskNode, GroupResourceBean storageGroup, RaidCardListEntity raidCardListEntity)
            throws FusionDirectorException {
        for (RaidCardEntity entity : raidCardListEntity.getMembers()) {
            AbstractApiWrapper raidCardApiWrapper = new RaidCardApiWrapper(fusionDirector);
            raidCardApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID(), entity.getDeviceID()));
            RaidCardBean raidCardBean = raidCardApiWrapper.call(RaidCardBean.class);
            String name = raidCardBean.getName();
            if (name == null) {
                final String url = raidCardApiWrapper.getRequestURL();
                logger.error("Raid card name is empty, check url to find problematic raid card data: " + url);
                continue;
            }
            if (name.startsWith("RAIDStorage")) {
                for (StorageControllerBean controllerBean : raidCardBean.getStorageControllers()) {
                    controllerBean.setDeviceID(raidCardBean.getDeviceID());
                    storageGroup.addChild(controllerBean);
                }
            }
        }
    }

    private void setNodeDrives(
            NodeEntity taskNode, NodeBean nodeBean, CatalogueEntity catalog, GroupResourceBean storageGroup) {
        DriveListEntity driveListEntity;
        AbstractApiWrapper driveListApiWrapper = new DriveListApiWrapper(fusionDirector);
        driveListApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        try {
            driveListEntity = driveListApiWrapper.call(DriveListEntity.class);
            if (catalog != null) {
                storageGroup.setHealthStatus(catalog.getStorageHealth());
            }
            if (driveListEntity == null || driveListEntity.getMembers() == null) {
                nodeBean.addChild(storageGroup);
                return;
            }
            for (DriveEntity entity : driveListEntity.getMembers()) {
                AbstractApiWrapper driveApiWrapper = new DriveApiWrapper(fusionDirector);
                driveApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID(), entity.getDeviceID()));
                DriveBean driveBean = driveApiWrapper.call(DriveBean.class);
                storageGroup.addChild(driveBean);
            }
            nodeBean.addChild(storageGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodeProcessor(NodeEntity taskNode, NodeBean nodeBean) {
        ProcessorListEntity processorListEntity;
        AbstractApiWrapper processApiWrapper = new ProcessorListApiWrapper(fusionDirector);
        processApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        try {
            processorListEntity = processApiWrapper.call(ProcessorListEntity.class);

            GroupResourceBean processorGroup = new GroupResourceBean("processorGroup", "Processor Group");
            processorGroup.addChildren(processorListEntity.getMembers());
            List<ProcessorBean> processorBeanList = processorListEntity.getProcessorBeans();
            HealthToolkit processorGroupHealthToolkit = new HealthToolkit();
            for (ProcessorBean processorBean : processorBeanList) {
                processorGroupHealthToolkit.pushHealth(processorBean.getStatus().getHealth());
            }
            processorGroup.setHealthStatus(processorGroupHealthToolkit.getHealth());
            nodeBean.addChild(processorGroup);
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setNodeStatisticsData(NodeEntity taskNode, NodeBean nodeBean) {
        NodeStatisticsEntity statistic;
        AbstractApiWrapper statisticApiWrapper = new NodeStatisticsApiWrapper(fusionDirector);
        statisticApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        try {
            statistic = statisticApiWrapper.call(NodeStatisticsEntity.class);
            nodeBean.setTemperature(statistic.getInletTemperature());
            nodeBean.setFanSpeedLevel(statistic.getFanSpeedLevel());
            nodeBean.setPowerConsumed(statistic.getPowerConsumed());
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private CatalogueEntity getCatalogueEntity(NodeEntity taskNode, NodeBean nodeBean) {
        AbstractApiWrapper catalogApiWrapper = new CatalogueApiWrapper(fusionDirector);
        catalogApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        CatalogueEntity catalog = null;
        try {
            catalog = catalogApiWrapper.call(CatalogueEntity.class);
            if (catalog != null) {
                nodeBean.setMemoryHealth(catalog.getMemoryHealth());
                nodeBean.setFanHealth(catalog.getFanHealth());
                nodeBean.setPowerHealth(catalog.getPowerHealth());
                nodeBean.setProcessorHealth(catalog.getProcessorHealth());
                nodeBean.setStorageHealth(catalog.getStorageHealth());
            }
        } catch (FusionDirectorException e) {
            logger.error(e.getMessage(), e);
            fusionDirector.isOffLineException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return catalog;
    }

    private NodeBean getNodeBean(NodeEntity taskNode) {
        NodeBean nodeBean = null;
        AbstractApiWrapper nodeApiWrapper = new NodeApiWrapper(fusionDirector);
        nodeApiWrapper.setPathVariable(Arrays.asList(taskNode.getDeviceID()));
        try {
            nodeBean = nodeApiWrapper.call(NodeBean.class);
            // workaround to fix inconsistent group between list and detail API
            nodeBean.setGroup(taskNode.getGroup());
            // workaround to fix inconsistent model between list and detail API
            if (nodeBean.getModel() == null || nodeBean.getModel().length() == 0) {
                nodeBean.setModel(taskNode.getModel());
            }
        } catch (FusionDirectorException e) {
            fusionDirector.isOffLineException(e);
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return nodeBean;
    }

    private void startCollectThreat(int name) {
        Thread collectThread = new Thread(this, "NodeCollector" + name);
        collectThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread tr, Throwable ex) {
                logger.error(tr.getName() + " : " + ex.getMessage());
            }
        });
        collectThread.start();
    }
}
