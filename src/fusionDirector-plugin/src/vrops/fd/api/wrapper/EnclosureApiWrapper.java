/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.api.wrapper;

import vrops.fd.service.bean.FusionDirector;

/**
 * EnclosureApiWrapper
 *
 * @since 2019-02-18
 */
public class EnclosureApiWrapper extends AbstractApiWrapper {
    public EnclosureApiWrapper(FusionDirector fd) {
        super(fd);
    }

    @Override
    public String getResourcePath() {
        return "/redfish/v1/rich/Enclosures/{0}";
    }
}
