/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.dsly.rxhttp.cache.strategy;

import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.model.RxHttpResponse;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * 如果缓存不存在才请求网络，否则使用缓存
 *
 * @author dsly
 * @date 2019-08-16
 */
public class NoneCacheRequestStrategy<T> extends BaseStrategy<T> {

    public NoneCacheRequestStrategy(String key, CacheMode mode, long cacheTime, Observable<Response<T>> observableSource) {
        super(key, mode, cacheTime, observableSource);
    }

    @Override
    public Observable<RxHttpResponse<T>> execute() {
        Observable<RxHttpResponse<T>> cacheObservable = generateCacheObservable(true);
        Observable<RxHttpResponse<T>> remoteObservable = generateRemoteObservable(false);
        return cacheObservable.switchIfEmpty(remoteObservable);
    }
}
