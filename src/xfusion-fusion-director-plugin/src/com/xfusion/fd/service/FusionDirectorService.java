/*
 * Copyright (c) xFusion Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.service;

import com.xfusion.fd.service.bean.EnclosureBean;
import com.xfusion.fd.service.bean.GroupBean;
import com.xfusion.fd.service.bean.GroupResourceBean;
import com.xfusion.fd.service.bean.NodeBean;
import com.xfusion.fd.service.bean.SwitchNodeBean;

import java.util.List;

/**
 * FusionDirectorService
 *
 * @since 2019-02-18
 */
public interface FusionDirectorService {
    List<NodeBean> getAllNodes();

    List<GroupBean> getAllNodeGroup();

    List<SwitchNodeBean> getAllSwitchNode();

    List<EnclosureBean> getAllEnclosure();

    List<GroupResourceBean> getClassifyGroup();

    String getVersion();

    String getNodeHealth(String deviceId);

    String getSwitchNodeHealth(String deviceId);

    NodeBean getBladeNode(String deviceId);
}
