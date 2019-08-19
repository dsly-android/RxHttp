package com.android.dsly.rxhttp;

import com.android.dsly.rxhttp.cookie.CookieJarImpl;
import com.android.dsly.rxhttp.gson.GsonFactory;
import com.android.dsly.rxhttp.interceptor.HeaderInterceptor;
import com.android.dsly.rxhttp.interceptor.HttpLoggingInterceptor;
import com.android.dsly.rxhttp.utils.HttpsUtils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author 陈志鹏
 * @date 2019-08-09
 */
public class RetrofitBuilder {

    private String baseUrl;

    private CallAdapter.Factory[] callAdapterFactory;

    private Converter.Factory[] converterFactory;

    private OkHttpClient okHttpClient;

    public RetrofitBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitBuilder setCallAdapterFactory(CallAdapter.Factory... callAdapterFactory) {
        this.callAdapterFactory = callAdapterFactory;
        return this;
    }

    public RetrofitBuilder setConverterFactory(Converter.Factory... converterFactory) {
        this.converterFactory = converterFactory;
        return this;
    }

    public RetrofitBuilder setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }


    public Retrofit build() {
        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(baseUrl);

        if (callAdapterFactory == null || callAdapterFactory.length <= 0) {
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        } else {
            for (CallAdapter.Factory factory : callAdapterFactory) {
                builder.addCallAdapterFactory(factory);
            }
        }

        if (converterFactory == null || converterFactory.length <= 0) {
            //可直接返回json数据
            builder.addConverterFactory(ScalarsConverterFactory.create())
                    //返回gson解析后的bean类
                    .addConverterFactory(GsonConverterFactory.create(GsonFactory.buildGson()));
        } else {
            for (Converter.Factory factory : converterFactory) {
                builder.addConverterFactory(factory);
            }
        }

        if (okHttpClient == null) {
            builder.client(createOkHttpClient());
        } else {
            builder.client(okHttpClient);
        }

        return builder.build();
    }

    private OkHttpClient createOkHttpClient() {
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

        if (BuildConfig.DEBUG){
            //log相关
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("RxHttp");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            //添加RxHttp默认debug日志
            builder.addInterceptor(loggingInterceptor);
        }

        //添加cookie
        if (RxHttp.getInstance().getCookieStore() != null) {
            builder.cookieJar(new CookieJarImpl(RxHttp.getInstance().getCookieStore()));
        }
        //添加头部拦截器，用于添加全局请求头
        builder.addInterceptor(new HeaderInterceptor(RxHttp.getInstance().getHeadersMap()));

        return builder.build();
    }
}
