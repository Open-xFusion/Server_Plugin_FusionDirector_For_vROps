/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.service;

import java.util.List;

/**
 * ResourceCollector
 *
 * @since 2019-02-18
 */
public interface ResourceCollector<V, T> {
    /**
     * 初始化任务列表
     *
     */
    void initTaskList();

    /**
     * 获取任务
     *
     * @return V 泛型对象
     */
    V getTask();

    /**
     * 收集任务
     *
     */
    void collect();

    /**
     * 获取收集结果
     *
     * @return list 获取收集结果对象列表
     */
    List<T> getCollectResult();

    /**
     * 收集资源结果
     *
     * @param t 传入收集对象
     */
    void onResouceCollected(T t);

    /**
     * 任务完成
     *
     */
    void finish();
}
