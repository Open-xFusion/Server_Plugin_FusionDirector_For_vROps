/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.api.wrapper;

import com.xfusion.fd.service.bean.FusionDirector;

/**
 * SwitchNodeListApiWrapper
 *
 * @since 2019-02-18
 */
public class SwitchNodeListApiWrapper extends AbstractApiWrapper {
    public SwitchNodeListApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/SwitchNodes";
    }
}
