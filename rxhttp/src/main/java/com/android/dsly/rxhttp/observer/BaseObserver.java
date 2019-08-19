package com.android.dsly.rxhttp.observer;

import com.android.dsly.rxhttp.utils.RxHttpLog;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Observer基类
 *
 * @author 陈志鹏
 * @date 2019-08-12
 */
public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        RxHttpLog.e(e);
        String errorMsg = handleException(e);
        onError(errorMsg);
    }

    /**
     * 接口请求失败回调
     * @param errorMsg
     */
    protected void onError(String errorMsg) {
        RxHttpLog.e(errorMsg);
    }

    public String handleException(Throwable e) {
        String msg = "未知错误";
        if (e instanceof UnknownHostException) {
            msg = "网络不可用";
        } else if (e instanceof SocketTimeoutException) {
            msg = "请求网络超时";
        } else if (e instanceof ConnectException) {
            msg = "网络连接异常，请检查您的网络状态，稍后重试！";
        } else if (e instanceof ConnectTimeoutException) {
            msg = "网络连接超时，请检查您的网络状态，稍后重试！";
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            msg = convertStatusCode(httpException);
        } else if (e instanceof android.net.ParseException || e instanceof JSONException
                || e instanceof JsonIOException || e instanceof JsonSerializer || e instanceof NotSerializableException) {
            msg = "数据解析错误";
        } else if (e instanceof ClassCastException) {
            msg = "类型转换错误";
        } else if (e instanceof NullPointerException) {
            msg = "空指针异常";
        } else if (e instanceof SSLHandshakeException) {
            msg = "证书验证失败";
        } else if (e instanceof IllegalStateException) {
            msg = e.getMessage();
        }
        return msg;
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}