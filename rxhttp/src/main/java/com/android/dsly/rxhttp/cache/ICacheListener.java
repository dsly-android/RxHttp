package com.android.dsly.rxhttp.cache;

import okhttp3.Headers;

/**
 * 缓存监听器
 *
 * @author 陈志鹏
 * @date 8/11/21
 */
public interface ICacheListener {

    /**
     * 是否需要缓存
     */
    <T> boolean isNeedCache(Headers responseHeaders, T data);
}