package com.android.dsly.rxhttp.test;

import android.app.Application;

import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheEntity;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.cookie.store.SPCookieStore;
import com.android.dsly.rxhttp.interceptor.HttpLoggingInterceptor;
import com.android.dsly.rxhttp.utils.HttpsUtils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;

/**
 * @author dsly
 * @date 2019-08-12
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //全局的读取超时时间
        builder.readTimeout(RxHttp.DEFAULT_MILLISECONDS / 2, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(RxHttp.DEFAULT_MILLISECONDS / 2, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(RxHttp.DEFAULT_MILLISECONDS / 2, TimeUnit.MILLISECONDS);

        //1、信任所有证书,不安全有风险（默认信任所有证书）
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        //2、使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksInputStream,"123456",cerInputStream);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        //设置Hostname校验规则，默认实现返回true，需要时候传入相应校验规则即可
        //builder.hostnameVerifier(null);

        if (BuildConfig.DEBUG) {
            //log相关
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("RxHttp");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            //添加RxHttp默认debug日志
            builder.addInterceptor(loggingInterceptor);
        }

        RxHttp.getInstance()
                .setBaseUrl("https://www.apiopen.top/")
                .setOkHttpClientBuild(RetrofitUrlManager.getInstance().with(builder))
//                .addCommonHeader("aaa","aaa")  //全局公共头
//                .addCommonHeaders(new LinkedHashMap<String, String>())   //全局公共头
                .setCookieType(new SPCookieStore(this)) //使用sp保持cookie，如果cookie不过期，则一直有效
//                .setCookieType(new MemoryCookieStore()) //使用内存保持cookie，app退出后，cookie消失
//                .setCookieType(new DBCookieStore(this)) ////使用数据库保持cookie，如果cookie不过期，则一直有效
                .setCacheMode(CacheMode.NO_CACHE) //缓存模式，默认不缓存
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)  //缓存过期时间，默认远不过期
                .init(this);
    }
}