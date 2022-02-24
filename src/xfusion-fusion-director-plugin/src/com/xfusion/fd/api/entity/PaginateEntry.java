/*
 * Copyright (c) xFusion Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.xfusion.fd.api.entity;

import java.util.List;

/**
 * PaginateEntry
 *
 * @since 2019-02-18
 */
public interface PaginateEntry<V> {
    boolean hasMoreEntry();

    List<V> getMembers();
}
