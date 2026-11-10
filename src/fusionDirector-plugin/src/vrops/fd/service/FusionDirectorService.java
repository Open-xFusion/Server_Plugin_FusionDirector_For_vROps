/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.service;

import vrops.fd.service.bean.EnclosureBean;
import vrops.fd.service.bean.GroupBean;
import vrops.fd.service.bean.GroupResourceBean;
import vrops.fd.service.bean.NodeBean;
import vrops.fd.service.bean.SwitchNodeBean;

import java.util.List;

/**
 * FusionDirectorService
 *
 * @since 2019-02-18
 */
public interface FusionDirectorService {
    /**
     * 获取所有节点
     *
     * @return list 节点对象
     */
    List<NodeBean> getAllNodes();

    /**
     * 获取所有节点的组
     *
     * @return list 组对象
     */
    List<GroupBean> getAllNodeGroup();

    /**
     * 获取所有转换节点
     *
     * @return list switch对象
     */
    List<SwitchNodeBean> getAllSwitchNode();

    /**
     * 获取所有机柜
     *
     * @return list 机柜对象
     */
    List<EnclosureBean> getAllEnclosure();

    /**
     * 获取子对象组
     *
     * @return list 组资源对象
     */
    List<GroupResourceBean> getClassifyGroup();

    /**
     * 获取版本
     *
     * @return version 版本号
     */
    String getVersion();

    /**
     * 获取节点的健康信息
     *
     * @param deviceId 设备id
     * @return nodeHealth 节点健康信息
     */
    String getNodeHealth(String deviceId);

    /**
     * 获取交换节点健康状况
     *
     * @param deviceId 设备id
     * @return switchNodeHealth 节点健康信息
     */
    String getSwitchNodeHealth(String deviceId);

    /**
     * 获取刀片节点信息
     *
     * @param deviceId 设备id
     * @return BladeNode 节点信息
     */
    NodeBean getBladeNode(String deviceId);
}
