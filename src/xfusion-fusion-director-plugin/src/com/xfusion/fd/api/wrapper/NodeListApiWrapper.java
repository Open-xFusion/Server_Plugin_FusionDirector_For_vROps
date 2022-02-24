/*
 * Copyright (c) xFusion Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.api.wrapper;

import com.xfusion.fd.service.bean.FusionDirector;

/**
 * NodeListApiWrapper
 *
 * @since 2019-02-18
 */
public class NodeListApiWrapper extends AbstractApiWrapper {
    public NodeListApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/Nodes";
    }
}
