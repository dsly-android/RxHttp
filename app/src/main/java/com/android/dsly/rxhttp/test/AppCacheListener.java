package com.android.dsly.rxhttp.test;

import com.android.dsly.rxhttp.cache.ICacheListener;

import okhttp3.Headers;

/**
 * @author 陈志鹏
 * @date 8/11/21
 */
public class AppCacheListener implements ICacheListener {

    @Override
    public <T> boolean isNeedCache(Headers responseHeaders, T data) {
        if (data instanceof BaseResponse) {
            if (((BaseResponse) data).getCode() == 200) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
