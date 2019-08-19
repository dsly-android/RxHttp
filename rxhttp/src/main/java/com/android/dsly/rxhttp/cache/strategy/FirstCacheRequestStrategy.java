package com.android.dsly.rxhttp.cache.strategy;

import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.model.RxHttpResponse;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

/**
 * 先使用缓存，不管是否存在，仍然请求网络
 *
 * @author 陈志鹏
 * @date 2019-08-16
 */
public class FirstCacheRequestStrategy<T> extends BaseStrategy<T> {

    public FirstCacheRequestStrategy(String key, CacheMode mode, long cacheTime, Observable<Response<T>> observableSource) {
        super(key, mode, cacheTime, observableSource);
    }

    @Override
    public Observable<RxHttpResponse<T>> execute() {
        Observable<RxHttpResponse<T>> cacheObservable = generateCacheObservable(true);
        Observable<RxHttpResponse<T>> remoteObservable = generateRemoteObservable(false);
        return Observable.concatDelayError(Arrays.asList(cacheObservable, remoteObservable))
                .filter(new Predicate<RxHttpResponse<T>>() {
                    @Override
                    public boolean test(RxHttpResponse<T> tRxHttpResponse) throws Exception {
                        return tRxHttpResponse.body() != null;
                    }
                });
    }
}
