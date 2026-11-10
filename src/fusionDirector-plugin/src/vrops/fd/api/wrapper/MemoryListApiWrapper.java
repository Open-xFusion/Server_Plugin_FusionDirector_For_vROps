/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.api.wrapper;

import vrops.fd.service.bean.FusionDirector;

/**
 * MemoryListApiWrapper
 *
 * @since 2019-02-18
 */
public class MemoryListApiWrapper extends AbstractApiWrapper {
    public MemoryListApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/Nodes/{0}/Memory";
    }
}
