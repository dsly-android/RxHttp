package com.android.dsly.rxhttp.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.dsly.rxhttp.IView;
import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.model.RxHttpResponse;
import com.android.dsly.rxhttp.observer.CommonObserver;
import com.android.dsly.rxhttp.observer.DownloadObserver;
import com.android.dsly.rxhttp.utils.TransformerUtils;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.io.File;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import retrofit2.Response;

public class MainActivity extends RxAppCompatActivity implements IView {

    private LoadingDialog loading_dialog;
    private BaseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        loading_dialog = new LoadingDialog(this);
        RetrofitUrlManager.getInstance().putDomain("aaa", "https://api.apiopen.top/");

        viewModel.getShowDialogLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showLoading();
                } else {
                    hideLoading();
                }
            }
        });
    }

    public void click1(View view) {
        RxHttp.createApi(CommonApi.class)
                .getNews()
                .compose(TransformerUtils.<String>pack(this))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<String>() {
                    @Override
                    protected void onSuccess(String s) {
                        Log.e("aaa", s);
                    }
                });
    }

    public void click2(View view) {
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>packResp(this))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<Response<String>>() {
                    @Override
                    protected void onSuccess(Response<String> response) {
                        Log.e("aaa", response.body());
                    }
                });
    }

    public void click3(View view) {
        RxHttp.uploadFile("http://t.xinhuo.com/index.php/Api/Pic/uploadPic",
                "", "/sdcard/yunk/yunkImg/5cc7b2a9ab22e.jpg")
                .compose(TransformerUtils.<String>pack(this))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<String>() {
                    @Override
                    protected void onSuccess(String s) {
                        Log.e("aaa", s);
                    }
                });
    }

    public void click4(View view) {
        HttpUrl url = HttpUrl.get("https://www.apiopen.top/journalismApi");
        Cookie.Builder builder = new Cookie.Builder();
        builder.domain("www.apiopen.top");
        builder.path("/");
        builder.name("aaa");
        builder.value("aaa");
        RxHttp.getInstance().getCookieStore().saveCookie(url, builder.build());

        click1(null);
    }

    public void click5(View view) {
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>noCachePackResp(this))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.body());
                    }
                });
    }

    public void click6(View view) {
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>cachePackResp(this, "click6", CacheMode.REQUEST_FAILED_READ_CACHE))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                    }
                });
    }

    public void click7(View view) {
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>cachePackResp(this, "click7", CacheMode.IF_NONE_CACHE_REQUEST))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("aaa", e.getMessage());
                    }
                });
    }

    public void click8(View view) {
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>cachePackResp(this, "click8", CacheMode.FIRST_CACHE_THEN_REQUEST))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("aaa", e.getMessage());
                    }
                });
    }

    public void click9(View view) {
        RxHttp.createApi(CommonApi.class)
                .touTiao()
                .compose(TransformerUtils.<String>packResp(this))
                .compose(RxUtils.showDialog(viewModel))
                .subscribe(new CommonObserver<Response<String>>() {
                    @Override
                    protected void onSuccess(Response<String> response) {
                        Log.e("aaa", response.body());
                    }
                });
    }

    public void click10(View view) {
        File file = new File(getExternalCacheDir(), "aaa.apk");
        RxHttp.downloadFile("https://34463b61b3ace6b08aa8d549200b179c.dlied1.cdntips.net/imtt.dd.qq.com/16891/apk/CD08ABAE3EEB07AE136108D5EA708E31.apk?mkey=5fdc461b1b9af0bb&f=1ea1&fsname=com.xiachufang_7.6.8_606.apk&csr=1bbd&cip=27.154.214.78&proto=https")
                .subscribe(new DownloadObserver(file.getAbsolutePath()) {
                    @Override
                    public void onSuccess(String path) {
                        Log.i("aaa","onFinish:" + path);
                    }

                    @Override
                    protected void onError(int code, String errorMsg) {
                        super.onError(code, errorMsg);
                        Log.i("aaa","onError");
                    }
                });
    }

    @Override
    public void showLoading() {
        loading_dialog.show();
    }

    @Override
    public void hideLoading() {
        loading_dialog.hide();
    }

    @Override
    public void killMyself() {
        finish();
    }
}