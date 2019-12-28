package com.android.dsly.rxhttp.test;

import com.android.dsly.rxhttp.model.RxHttpResponse;
import com.android.dsly.rxhttp.observer.BaseObserver;

import retrofit2.Response;

/**
 * @author dsly
 * @date 2019-08-12
 */
public abstract class DataObserver<T> extends BaseObserver<T> {

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        Object baseResponse = null;
        if (t instanceof Response) {
            baseResponse = ((Response) t).body();
        } else if (t instanceof RxHttpResponse) {
            baseResponse = ((RxHttpResponse) t).body();
        } else {
            baseResponse = t;
        }
        if (baseResponse instanceof BaseResponse) {
            //可以根据需求对code统一处理
            if (((BaseResponse) baseResponse).getCode() == 200) {
                onSuccess(t);
            } else {
                onError(new IllegalStateException(((BaseResponse) baseResponse).getMsg()));
            }
        } else {
            onSuccess(t);
        }
    }
}
