package com.android.dsly.rxhttp.cache.strategy;


import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheEntity;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.db.CacheManager;
import com.android.dsly.rxhttp.model.HttpHeaders;
import com.android.dsly.rxhttp.model.RxHttpResponse;
import com.android.dsly.rxhttp.utils.RxHttpLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import retrofit2.Response;

/**
 * @author 陈志鹏
 * @date 2019-08-15
 */
public abstract class BaseStrategy<T> {

    protected CacheMode mCacheMode; //缓存模式
    protected long mCacheTime;      //缓存过期时间,默认永不过期
    protected String mKey;           //缓存key
    protected Observable<Response<T>> mSourceObservable;

    public BaseStrategy(String key, CacheMode mode, long cacheTime, Observable<Response<T>> observableSource) {
        if (mode == null) {
            mCacheMode = RxHttp.getInstance().getCacheMode();
        } else {
            mCacheMode = mode;
        }
        if (cacheTime <= -1) {
            cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        }
        this.mCacheTime = cacheTime;
        mKey = key;
        mSourceObservable = observableSource;
    }

    /**
     * 缓存功能实现
     *
     * @return
     */
    public abstract Observable<RxHttpResponse<T>> execute();

    /**
     * 生成CacheObservable
     */
    protected Observable<RxHttpResponse<T>> generateCacheObservable(final boolean needEmpty) {
        final Observable<RxHttpResponse<T>> observable = Observable.create(new ObservableOnSubscribe<RxHttpResponse<T>>() {
            @Override
            public void subscribe(ObservableEmitter<RxHttpResponse<T>> emitter) throws Exception {
                CacheEntity<T> entity = getCache();
                if (!emitter.isDisposed()) {
                    if (entity != null) {
                        emitter.onNext(new RxHttpResponse<T>(null, entity.getData(), null, true, entity.getResponseHeaders().headersMap));
                        emitter.onComplete();
                    } else {
                        emitter.onError(new NullPointerException("Not find the key corresponding to the cache"));
                    }
                }
            }
        });
        if (needEmpty) {
            return observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends RxHttpResponse<T>>>() {
                @Override
                public ObservableSource<? extends RxHttpResponse<T>> apply(Throwable throwable) throws Exception {
                    RxHttpLog.e("cacheneedempty");
                    return Observable.empty();
                }
            });
        } else {
            return observable;
        }
    }

    /**
     * 生成RemoteObservable
     */
    protected Observable<RxHttpResponse<T>> generateRemoteObservable(boolean needEmpty) {
        Observable<RxHttpResponse<T>> observable = mSourceObservable.map(new Function<Response<T>, RxHttpResponse<T>>() {
            @Override
            public RxHttpResponse<T> apply(Response<T> tResponse) throws Exception {
                saveCache(tResponse.headers(), tResponse.body());
                return new RxHttpResponse<>(tResponse.raw(), tResponse.body(), tResponse.errorBody(), false, null);
            }
        });
        if (needEmpty) {
            return observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends RxHttpResponse<T>>>() {
                @Override
                public ObservableSource<? extends RxHttpResponse<T>> apply(Throwable throwable) throws Exception {
                    RxHttpLog.e("remoteneedempty");
                    return Observable.empty();
                }
            });
        } else {
            return observable;
        }
    }

    /**
     * 从数据库中获取保存的数据
     */
    protected CacheEntity<T> getCache() {
        if (mCacheMode == CacheMode.NO_CACHE) {
            return null;
        }
        CacheEntity<T> cacheEntity = (CacheEntity<T>) CacheManager.getInstance().get(mKey);
        if (cacheEntity != null && cacheEntity.checkExpire(mCacheTime, System.currentTimeMillis())) {
            cacheEntity.setExpire(true);
        }
        if (cacheEntity == null || cacheEntity.isExpire() || cacheEntity.getData() == null || cacheEntity.getResponseHeaders() == null) {
            cacheEntity = null;
        }
        return cacheEntity;
    }

    /**
     * 将请求结果保存到数据库
     */
    protected void saveCache(final Headers responseHeaders, final T data) {
        if (mCacheMode == CacheMode.NO_CACHE) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                //将response中所有的头存入 HttpHeaders，原因是写入数据库的对象需要实现序列化，而默认的Header没有序列化
                HttpHeaders headers = new HttpHeaders();
                for (String headerName : responseHeaders.names()) {
                    headers.put(headerName, responseHeaders.get(headerName));
                }

                //构建缓存实体对象
                CacheEntity<T> cacheEntity = new CacheEntity<>();
                cacheEntity.setKey(mKey);
                cacheEntity.setData(data);
                cacheEntity.setLocalExpire(System.currentTimeMillis());
                cacheEntity.setResponseHeaders(headers);

                CacheManager.getInstance().replace(mKey, cacheEntity);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        RxHttpLog.e("key：" + mKey + "的数据缓存结果为：" + aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        RxHttpLog.e(throwable);
                    }
                });
    }
}
