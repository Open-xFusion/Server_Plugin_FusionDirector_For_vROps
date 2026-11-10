/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.service.bean;

/**
 * GroupResourceBean
 *
 * @since 2019-02-18
 */
public class GroupResourceBean extends BaseResource {
    private String resourceName;

    private String resourceLabel;

    private String healthStatus = null;

    /**
     * GroupResourceBean构造函数
     *
     * @param resourceName resourceName
     * @param resourceLabel resourceLabel
     */
    public GroupResourceBean(String resourceName, String resourceLabel) {
        super();
        this.resourceName = resourceName;
        this.resourceLabel = resourceLabel;
    }

    @Override
    public String getResourceName() {
        return this.resourceName;
    }

    @Override
    public String getResourceLabel() {
        return this.resourceLabel;
    }

    @Override
    public String getResourceIdentifier() {
        return this.resourceLabel;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    @Override
    public void setAttributes() {
        if (this.healthStatus != null) {
            setStringMetric("healthStatus", this.healthStatus);
        }
    }

    @Override
    public boolean allowRename() {
        return true;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceLabel(String resourceLabel) {
        this.resourceLabel = resourceLabel;
    }
}
