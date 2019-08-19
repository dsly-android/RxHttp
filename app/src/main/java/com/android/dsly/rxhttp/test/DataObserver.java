package com.android.dsly.rxhttp.test;

import com.android.dsly.rxhttp.observer.BaseObserver;

/**
 * @author 陈志鹏
 * @date 2019-08-12
 */
public abstract class DataObserver<T> extends BaseObserver<BaseResponse<T>> {

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract void onSuccess(T t);

    @Override
    public void onNext(BaseResponse<T> tBaseResponse) {
        //可以根据需求对code统一处理
        if (tBaseResponse.getCode() == 200) {
            onSuccess(tBaseResponse.getData());
        } else {
            onError(new IllegalStateException("code:" + tBaseResponse.getCode() + "     msg:" + tBaseResponse.getMsg()));
        }
    }
}
