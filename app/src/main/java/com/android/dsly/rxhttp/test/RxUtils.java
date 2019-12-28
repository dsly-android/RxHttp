package com.android.dsly.rxhttp.test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author 陈志鹏
 * @date 2019-12-27
 */
public class RxUtils {

    /**
     * 显示对话框
     * @param viewModel
     * @return
     */
    public static ObservableTransformer showDialog(final BaseViewModel viewModel) {
        return new ObservableTransformer<Observable, Observable>() {
            @Override
            public ObservableSource<Observable> apply(Observable<Observable> upstream) {
                return upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        viewModel.setShowDialog(true);
                    }
                })
                        .doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                viewModel.setShowDialog(false);
                            }
                        });
            }
        };
    }
}
