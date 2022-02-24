/*
 * Copyright (c) xFusion Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.api.wrapper;

import com.xfusion.fd.service.bean.FusionDirector;

/**
 * FusionDirectorVersionWrapper
 *
 * @since 2019-02-18
 */
public class FusionDirectorVersionWrapper extends AbstractApiWrapper {
    public FusionDirectorVersionWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/Appliance/Version";
    }
}
