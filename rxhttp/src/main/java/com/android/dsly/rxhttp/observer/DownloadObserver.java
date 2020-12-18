package com.android.dsly.rxhttp.observer;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ThreadUtils;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;

/**
 * @author 陈志鹏
 * @date 2020/12/18
 */
public abstract class DownloadObserver extends BaseObserver<ResponseBody> {

    private String mFilePath;

    public DownloadObserver(String filePath) {
        mFilePath = filePath;
    }

    @Override
    public void onNext(@NonNull ResponseBody responseBody) {
        final boolean isSuccess = FileIOUtils.writeFileFromIS(mFilePath, responseBody.byteStream());
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    onSuccess(mFilePath);
                } else {
                    onError(0, "下载失败");
                }
            }
        });
    }

    /**
     * 下载完成，运行在主线程
     */
    public abstract void onSuccess(String path);
}