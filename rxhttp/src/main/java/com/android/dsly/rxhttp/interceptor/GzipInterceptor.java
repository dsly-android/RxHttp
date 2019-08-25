package com.android.dsly.rxhttp.interceptor;

import com.android.dsly.rxhttp.utils.GzipUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 解压接口返回的压缩数据
 *
 * @author dsly
 * @date 2019-08-25
 */
public class GzipInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 200) {
            //这里是网络拦截器，可以做错误处理
            MediaType mediaType = response.body().contentType();
            byte[] data = response.body().bytes();
            if (GzipUtils.isGzip(response.headers())) {
                //请求头显示有gzip，需要解压
                data = GzipUtils.uncompress(data);
            }
            //创建一个新的responseBody，返回进行处理
            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, data))
                    .build();
        } else {
            return response;
        }
    }
}