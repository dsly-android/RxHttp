package com.android.dsly.rxhttp.interceptor;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器  统一添加请求头使用
 *
 * @author chenzhipeng
 */
public class HeaderInterceptor implements Interceptor {

    private Map<String, Object> mHeadersMap;

    public HeaderInterceptor(Map<String, Object> headersMap) {
        mHeadersMap = headersMap;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (mHeadersMap == null || mHeadersMap.isEmpty()) {
            return chain.proceed(request);
        } else {
            Response response = chain.proceed(request.newBuilder()
                    .headers(buildHeaders(request, mHeadersMap))
                    .build());
            return response;
        }
    }

    private Headers buildHeaders(Request request, Map<String, Object> headerMap) {
        Headers headers = request.headers();
        if (headers != null) {
            Headers.Builder builder = headers.newBuilder();
            for (String key : headerMap.keySet()) {
                builder.add(key, headerMap.get(key) + "");
            }
            return builder.build();
        } else {
            return headers;
        }
    }
}
