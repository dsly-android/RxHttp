package com.android.dsly.rxhttp.observer;

/**
 * 通用Observer
 *
 * @author dsly
 * @date 2019-08-12
 */
public abstract class CommonObserver<T> extends BaseObserver<T> {

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }
}
