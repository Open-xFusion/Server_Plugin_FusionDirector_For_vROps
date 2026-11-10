/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.api.wrapper;

import vrops.fd.service.bean.FusionDirector;

/**
 * NetworkAdapterListApiWrapper
 *
 * @since 2019-02-18
 */
public class NetworkAdapterListApiWrapper extends AbstractApiWrapper {
    public NetworkAdapterListApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/Nodes/{0}/NetworkAdapter";
    }
}
