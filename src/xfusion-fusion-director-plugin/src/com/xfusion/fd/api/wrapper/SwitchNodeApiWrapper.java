/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.api.wrapper;

import com.xfusion.fd.service.bean.FusionDirector;

/**
 * SwitchNodeApiWrapper
 *
 * @since 2019-02-18
 */
public class SwitchNodeApiWrapper extends AbstractApiWrapper {
    public SwitchNodeApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/SwitchNodes/{0}";
    }
}
