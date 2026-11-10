/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.api.entity;

import java.util.List;

/**
 * PaginateEntry
 *
 * @since 2019-02-18
 */
public interface PaginateEntry<V> {
    /**
     * hasMoreEntry
     *
     * @return boolean 类型
     */
    boolean hasMoreEntry();

    /**
     * getMembers
     *
     * @return list<> 类型
     */
    List<V> getMembers();
}
