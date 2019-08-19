package com.android.dsly.rxhttp.cache.strategy;

import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.model.RxHttpResponse;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import retrofit2.Response;

/**
 * 不缓存
 *
 * @author dsly
 * @date 2019-08-16
 */
public class NoCacheStrategy<T> extends BaseStrategy<T> {

    public NoCacheStrategy(String key, CacheMode mode, long cacheTime, Observable<Response<T>> observableSource) {
        super(key, mode, cacheTime, observableSource);
    }

    @Override
    public Observable<RxHttpResponse<T>> execute() {
        return mSourceObservable.map(new Function<Response<T>, RxHttpResponse<T>>() {
            @Override
            public RxHttpResponse<T> apply(Response<T> tResponse) throws Exception {
                return new RxHttpResponse<T>(tResponse.raw(), tResponse.body(), tResponse.errorBody(), false, null);
            }
        });
    }
}
