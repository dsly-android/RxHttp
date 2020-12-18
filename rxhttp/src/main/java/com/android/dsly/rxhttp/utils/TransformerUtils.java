package com.android.dsly.rxhttp.utils;

import com.android.dsly.rxhttp.RetryWithDelay;
import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.cache.strategy.BaseStrategy;
import com.android.dsly.rxhttp.cache.strategy.FirstCacheRequestStrategy;
import com.android.dsly.rxhttp.cache.strategy.NoCacheStrategy;
import com.android.dsly.rxhttp.cache.strategy.NoneCacheRequestStrategy;
import com.android.dsly.rxhttp.cache.strategy.RequestFailedCacheStrategy;
import com.android.dsly.rxhttp.model.RxHttpResponse;
import com.trello.rxlifecycle3.LifecycleProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author dsly
 * @date 2019-08-13
 */
public class TransformerUtils {

    /**
     * 缓存功能
     */
    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> noCache() {
        return cache(null, CacheMode.NO_CACHE);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cache(String key) {
        return cache(key, RxHttp.getInstance().getCacheMode());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cache(String key, CacheMode cacheMode) {
        return cache(key, cacheMode, RxHttp.getInstance().getCacheTime());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cache(final String key, final CacheMode cacheMode, final long cacheTime) {
        return new ObservableTransformer<Response<T>, RxHttpResponse<T>>() {
            @Override
            public ObservableSource<RxHttpResponse<T>> apply(Observable<Response<T>> upstream) {
                BaseStrategy<T> strategy = prepareStrategy(key, cacheMode, cacheTime, upstream);
                return strategy.execute();
            }
        };
    }

    /**
     * 重试、切换线程、绑定生命周期
     *
     * @return
     */
    public static <T> ObservableTransformer<T, T> pack() {
        return pack(null, true);
    }

    public static <T> ObservableTransformer<T, T> pack(LifecycleProvider provider) {
        return pack(provider, true);
    }

    public static <T> ObservableTransformer<T, T> pack(final LifecycleProvider provider, final boolean isRetry) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = upstream;
                if (isRetry) {
                    observable = upstream.retryWhen(new RetryWithDelay(3, 2));
                }
                observable = observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                if (provider == null) {
                    return observable;
                } else {
                    return observable.compose(RxLifecycleUtils.<T>bindToLifecycle(provider));
                }
            }
        };
    }

    public static <T> ObservableTransformer<Response<T>, Response<T>> packResp() {
        return packResp(null);
    }

    public static <T> ObservableTransformer<Response<T>, Response<T>> packResp(final LifecycleProvider provider) {
        return new ObservableTransformer<Response<T>, Response<T>>() {
            @Override
            public ObservableSource<Response<T>> apply(Observable<Response<T>> upstream) {
                Observable<Response<T>> observable = upstream
                        .retryWhen(new RetryWithDelay(3, 2))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                if (provider == null) {
                    return observable;
                } else {
                    return observable.compose(RxLifecycleUtils.<Response<T>>bindToLifecycle(provider));
                }
            }
        };
    }

    /**
     * 缓存、重试、切换线程、绑定生命周期
     */
    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> noCachePackResp() {
        return cachePackResp(null, null, CacheMode.NO_CACHE);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> noCachePackResp(LifecycleProvider provider) {
        return cachePackResp(provider, null, CacheMode.NO_CACHE);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(String key) {
        return cachePackResp(null, key);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(LifecycleProvider provider, String key) {
        return cachePackResp(provider, key, RxHttp.getInstance().getCacheMode());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(String key, CacheMode cacheMode) {
        return cachePackResp(null, key, cacheMode);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(LifecycleProvider provider, String key, CacheMode cacheMode) {
        return cachePackResp(provider, key, cacheMode, RxHttp.getInstance().getCacheTime());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(String key, CacheMode cacheMode, long cacheTime) {
        return cachePackResp(null, key, cacheMode, cacheTime);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(final LifecycleProvider provider, final String key, final CacheMode cacheMode, final long cacheTime) {
        return new ObservableTransformer<Response<T>, RxHttpResponse<T>>() {
            @Override
            public ObservableSource<RxHttpResponse<T>> apply(Observable<Response<T>> upstream) {
                BaseStrategy<T> strategy = prepareStrategy(key, cacheMode, cacheTime, upstream);
                Observable<RxHttpResponse<T>> observable = strategy.execute()
                        .retryWhen(new RetryWithDelay(3, 2))
                        .subscribeOn(Schedulers.io());
                return observable.observeOn(AndroidSchedulers.mainThread())
                        .compose(RxLifecycleUtils.<RxHttpResponse<T>>bindToLifecycle(provider));
            }
        };
    }

    public static <T> BaseStrategy prepareStrategy(String key, CacheMode cacheMode, long cacheTime, Observable<Response<T>> observableSource) {
        switch (cacheMode) {
            case NO_CACHE:
                return new NoCacheStrategy(key, cacheMode, cacheTime, observableSource);
            case IF_NONE_CACHE_REQUEST:
                return new NoneCacheRequestStrategy(key, cacheMode, cacheTime, observableSource);
            case REQUEST_FAILED_READ_CACHE:
                return new RequestFailedCacheStrategy(key, cacheMode, cacheTime, observableSource);
            case FIRST_CACHE_THEN_REQUEST:
                return new FirstCacheRequestStrategy(key, cacheMode, cacheTime, observableSource);
            default:
                return null;
        }
    }
}
