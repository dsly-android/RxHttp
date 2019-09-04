package com.android.dsly.rxhttp;

import android.app.Application;

import com.android.dsly.rxhttp.api.UploadFileApi;
import com.android.dsly.rxhttp.cache.CacheEntity;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.cookie.CookieJarImpl;
import com.android.dsly.rxhttp.cookie.store.CookieStore;
import com.android.dsly.rxhttp.interceptor.GzipInterceptor;
import com.android.dsly.rxhttp.interceptor.HeaderInterceptor;
import com.android.dsly.rxhttp.utils.RxHttpLog;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author dsly
 * @date 2019-08-09
 */
public class RxHttp {

    public static final long DEFAULT_MILLISECONDS = 60000;      //默认的超时时间
    /**
     * 缓存ServiceApi不会重复创建ServiceApi对象
     */
    private static ArrayList<Object> mServiceApiCache;
    private static Retrofit mRetrofit;
    private CallAdapter.Factory[] mCallAdapterFactory;
    private Converter.Factory[] mConverterFactory;
    private OkHttpClient.Builder mBuilder;
    private String mBaseUrl;
    private LinkedHashMap<String, Object> mHeadersMap;  //全局头
    private CookieStore mCookieStore;
    private Application mApp;     //全局上下文
    private CacheMode mCacheMode; //全局缓存模式
    private long mCacheTime;      //全局缓存过期时间,默认永不过期，单位毫秒

    public static RxHttp getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final RxHttp INSTANCE = new RxHttp();
    }

    private RxHttp() {
        mServiceApiCache = new ArrayList<>();
        //默认不缓存
        mCacheMode = CacheMode.NO_CACHE;
        //默认永不过期
        mCacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
    }

    public void init(Application app) {
        mApp = app;

        RxHttpLog.getConfig().setLogSwitch(BuildConfig.DEBUG);

        //添加gzip请求头
        addCommonHeader("Accept-Encoding", "gzip,deflate");
        if (mBuilder != null) {
            //添加cookie
            if (mCookieStore != null) {
                mBuilder.cookieJar(new CookieJarImpl(mCookieStore));
            }
            //添加头部拦截器，用于添加全局请求头
            mBuilder.addInterceptor(new HeaderInterceptor(mHeadersMap));
            //添加返回数据解压拦截器
            mBuilder.addInterceptor(new GzipInterceptor());
        }
        mRetrofit = new RetrofitBuilder()
                .setBaseUrl(mBaseUrl)
                .setCallAdapterFactory(mCallAdapterFactory)
                .setConverterFactory(mConverterFactory)
                .setOkHttpClient(mBuilder == null ? null : mBuilder.build())
                .build();
    }

    public static <A> A createApi(Class<A> apiClass) {
        for (int i = 0; i < mServiceApiCache.size(); i++) {
            Object o = mServiceApiCache.get(i);
            if (o.getClass().getSimpleName().equals(apiClass.getSimpleName())) {
                return (A) o;
            }
        }
        A api = mRetrofit.create(apiClass);
        mServiceApiCache.add(api);
        return api;
    }

    public Application getApp() {
        return mApp;
    }

    /**
     * 必传
     *
     * @param baseUrl
     * @return
     */
    public RxHttp setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
        return this;
    }

    public RxHttp setCallAdapterFactory(CallAdapter.Factory... callAdapterFactory) {
        this.mCallAdapterFactory = callAdapterFactory;
        return this;
    }

    public RxHttp setConverterFactory(Converter.Factory... converterFactory) {
        this.mConverterFactory = converterFactory;
        return this;
    }

    public RxHttp setOkHttpClientBuild(OkHttpClient.Builder builder) {
        this.mBuilder = builder;
        return this;
    }

    /**
     * 添加保存cookie的实现类
     *
     * @param cookieStore
     * @return
     */
    public RxHttp setCookieType(CookieStore cookieStore) {
        mCookieStore = cookieStore;
        return this;
    }

    /**
     * 获取cookie的保存类
     */
    public CookieStore getCookieStore() {
        return mCookieStore;
    }

    /**
     * 添加全局公共请求头
     */
    public RxHttp addCommonHeader(String key, Object value) {
        if (mHeadersMap == null) {
            mHeadersMap = new LinkedHashMap<>();
        }
        mHeadersMap.put(key, value);
        return this;
    }

    /**
     * 添加全局公共请求头
     */
    public RxHttp addCommonHeaders(Map<String, Object> headersMap) {
        if (mHeadersMap == null) {
            mHeadersMap = new LinkedHashMap<>();
        }
        if (headersMap != null && !headersMap.isEmpty()) {
            mHeadersMap.putAll(headersMap);
        }
        return this;
    }

    public LinkedHashMap<String, Object> getHeadersMap() {
        return mHeadersMap;
    }

    /**
     * 全局的缓存模式
     */
    public RxHttp setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /**
     * 获取全局的缓存模式
     */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /**
     * 全局的缓存过期时间
     */
    public RxHttp setCacheTime(long cacheTime) {
        if (cacheTime <= -1) {
            cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        }
        mCacheTime = cacheTime;
        return this;
    }

    /**
     * 获取全局的缓存过期时间
     */
    public long getCacheTime() {
        return mCacheTime;
    }

    /**
     * 上传一个文件
     *
     * @param uploadUrl 上传文件的服务器url
     * @param fileKey   后台协定的接受图片的key（没特殊要求就可以随便写）
     * @param filePath  文件路径
     * @return Observable
     */
    public static Observable<String> uploadFile(String uploadUrl, String fileKey, String filePath) {
        return uploadFile(uploadUrl, fileKey, null, filePath);
    }

    /**
     * 上传一个文件
     *
     * @param uploadUrl 上传文件的服务器url
     * @param fileKey   后台协定的接受图片的key（没特殊要求就可以随便写）
     * @param paramsMap 普通参数
     * @param filePath  文件路径
     * @return Observable
     */
    public static Observable<String> uploadFile(String uploadUrl, String fileKey, Map<String, Object> paramsMap, String filePath) {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        return uploadFiles(uploadUrl, fileKey, paramsMap, filePaths);
    }

    /**
     * 上传多个文件
     *
     * @param uploadUrl 上传图片的服务器url
     * @param fileKey   后台协定的接受图片的key（没特殊要求就可以随便写）
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<String> uploadFiles(String uploadUrl, String fileKey, List<String> filePaths) {
        return uploadFiles(uploadUrl, fileKey, null, filePaths);
    }

    /**
     * 上传多个文件
     *
     * @param uploadUrl 上传图片的服务器url
     * @param fileKey   后台协定的接受图片的key（没特殊要求就可以随便写）
     * @param paramsMap 普通参数
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<String> uploadFiles(String uploadUrl, String fileKey, Map<String, Object> paramsMap, List<String> filePaths) {
        return uploadFilesWithParams(uploadUrl, fileKey, paramsMap, filePaths);
    }

    /**
     * 文件和参数同时上传的请求
     *
     * @param uploadUrl 上传图片的服务器url
     * @param fileKey   后台协定的接受图片的key（没特殊要求就可以随便写）
     * @param paramsMap 普通参数
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<String> uploadFilesWithParams(String uploadUrl, String fileKey, Map<String, Object> paramsMap, List<String> filePaths) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (null != paramsMap) {
            for (String key : paramsMap.keySet()) {
                builder.addFormDataPart(key, paramsMap.get(key) + "");
            }
        }

        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //"fileName"+i 后台接收图片流的参数名
            builder.addFormDataPart(fileKey, file.getName(), imageBody);
        }

        List<MultipartBody.Part> parts = builder.build().parts();

        return createApi(UploadFileApi.class)
                .uploadFiles(uploadUrl, parts);
    }
}
