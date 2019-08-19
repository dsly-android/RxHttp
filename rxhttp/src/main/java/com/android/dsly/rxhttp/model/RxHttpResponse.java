package com.android.dsly.rxhttp.model;

import java.util.HashMap;

import androidx.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.ResponseBody;

/**
 * @author dsly
 * @date 2019-08-15
 */
public class RxHttpResponse<T> {

    private boolean isFromCache; //数据是否来自缓存

    private final okhttp3.Response rawResponse;
    @Nullable
    private final T body;
    @Nullable
    private final ResponseBody errorBody;
    //从缓存中获取的响应头参数
    private HashMap<String, String> headersMap;

    public RxHttpResponse(okhttp3.Response rawResponse, @Nullable T body,
                          @Nullable ResponseBody errorBody, boolean isFromCache, HashMap<String, String> headersMap) {
        this.rawResponse = rawResponse;
        this.body = body;
        this.errorBody = errorBody;
        this.isFromCache = isFromCache;
        this.headersMap = headersMap;
    }

    /**
     * The raw response from the HTTP client.
     */
    public okhttp3.Response raw() {
        return rawResponse;
    }

    /**
     * HTTP status code.
     */
    public int code() {
        return rawResponse.code();
    }

    /**
     * HTTP status message or null if unknown.
     */
    public String message() {
        return rawResponse.message();
    }

    /**
     * HTTP headers.
     */
    public Headers headers() {
        return rawResponse.headers();
    }

    /**
     * Returns true if {@link #code()} is in the range [200..300).
     */
    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    /**
     * The deserialized response body of a {@linkplain #isSuccessful() successful} response.
     */
    public @Nullable
    T body() {
        return body;
    }

    /**
     * The raw response body of an {@linkplain #isSuccessful() unsuccessful} response.
     */
    public @Nullable
    ResponseBody errorBody() {
        return errorBody;
    }

    public boolean isFromCache() {
        return isFromCache;
    }

    public void setFromCache(boolean fromCache) {
        isFromCache = fromCache;
    }

    public HashMap<String, String> getHeadersMap() {
        return headersMap;
    }

    @Override
    public String toString() {
        return rawResponse.toString();
    }
}
