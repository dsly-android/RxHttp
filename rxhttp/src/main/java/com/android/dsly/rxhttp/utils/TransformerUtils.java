package com.android.dsly.rxhttp.utils;

import com.android.dsly.rxhttp.IView;
import com.android.dsly.rxhttp.RetryWithDelay;
import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.cache.strategy.BaseStrategy;
import com.android.dsly.rxhttp.cache.strategy.FirstCacheRequestStrategy;
import com.android.dsly.rxhttp.cache.strategy.NoCacheStrategy;
import com.android.dsly.rxhttp.cache.strategy.NoneCacheRequestStrategy;
import com.android.dsly.rxhttp.cache.strategy.RequestFailedCacheStrategy;
import com.android.dsly.rxhttp.model.RxHttpResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author 陈志鹏
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
    public static <T> ObservableTransformer<T, T> pack(IView view) {
        return pack(view, false);
    }

    public static <T> ObservableTransformer<T, T> pack(final IView view, final boolean showLoading) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = upstream
                        .retryWhen(new RetryWithDelay(3, 2))
                        .subscribeOn(Schedulers.io());
                if (showLoading) {
                    return observable.doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            view.showLoading();
                        }
                    })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(new Action() {
                                @Override
                                public void run() throws Exception {
                                    view.hideLoading();
                                }
                            })
                            .compose(RxLifecycleUtils.<T>bindToLifecycle(view));
                } else {
                    return observable.observeOn(AndroidSchedulers.mainThread())
                            .compose(RxLifecycleUtils.<T>bindToLifecycle(view));
                }
            }
        };
    }

    public static <T> ObservableTransformer<Response<T>, Response<T>> packResp(IView view) {
        return packResp(view, false);
    }

    public static <T> ObservableTransformer<Response<T>, Response<T>> packResp(final IView view, final boolean showLoading) {
        return new ObservableTransformer<Response<T>, Response<T>>() {
            @Override
            public ObservableSource<Response<T>> apply(Observable<Response<T>> upstream) {
                Observable<Response<T>> observable = upstream
                        .retryWhen(new RetryWithDelay(3, 2))
                        .subscribeOn(Schedulers.io());
                if (showLoading) {
                    return observable.doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            view.showLoading();
                        }
                    })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(new Action() {
                                @Override
                                public void run() throws Exception {
                                    view.hideLoading();
                                }
                            })
                            .compose(RxLifecycleUtils.<Response<T>>bindToLifecycle(view));
                } else {
                    return observable.observeOn(AndroidSchedulers.mainThread())
                            .compose(RxLifecycleUtils.<Response<T>>bindToLifecycle(view));
                }
            }
        };
    }

    /**
     * 缓存、重试、切换线程、绑定生命周期
     */
    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> noCachePackResp(IView view) {
        return noCachePackResp(view, false);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> noCachePackResp(IView view, boolean showLoading) {
        return cachePackResp(view, showLoading, null, CacheMode.NO_CACHE);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(IView view, String key) {
        return cachePackResp(view, false, key);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(IView view, boolean showLoading, String key) {
        return cachePackResp(view, showLoading, key, RxHttp.getInstance().getCacheMode());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(IView view, String key, CacheMode cacheMode) {
        return cachePackResp(view, false, key, cacheMode, RxHttp.getInstance().getCacheTime());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(IView view, boolean showLoading, String key, CacheMode cacheMode) {
        return cachePackResp(view, showLoading, key, cacheMode, RxHttp.getInstance().getCacheTime());
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(IView view, String key, CacheMode cacheMode, long cacheTime) {
        return cachePackResp(view, false, key, cacheMode, cacheTime);
    }

    public static <T> ObservableTransformer<Response<T>, RxHttpResponse<T>> cachePackResp(final IView view, final boolean showLoading, final String key, final CacheMode cacheMode, final long cacheTime) {
        return new ObservableTransformer<Response<T>, RxHttpResponse<T>>() {
            @Override
            public ObservableSource<RxHttpResponse<T>> apply(Observable<Response<T>> upstream) {
                BaseStrategy<T> strategy = prepareStrategy(key, cacheMode, cacheTime, upstream);
                Observable<RxHttpResponse<T>> observable = strategy.execute()
                        .retryWhen(new RetryWithDelay(3, 2))
                        .subscribeOn(Schedulers.io());
                if (showLoading) {
                    return observable.doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            view.showLoading();
                        }
                    })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(new Action() {
                                @Override
                                public void run() throws Exception {
                                    view.hideLoading();
                                }
                            })
                            .compose(RxLifecycleUtils.<RxHttpResponse<T>>bindToLifecycle(view));
                } else {
                    return observable.observeOn(AndroidSchedulers.mainThread())
                            .compose(RxLifecycleUtils.<RxHttpResponse<T>>bindToLifecycle(view));
                }
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
