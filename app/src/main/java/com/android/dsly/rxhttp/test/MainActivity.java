package com.android.dsly.rxhttp.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.dsly.rxhttp.IView;
import com.android.dsly.rxhttp.RxHttp;
import com.android.dsly.rxhttp.cache.CacheMode;
import com.android.dsly.rxhttp.model.RxHttpResponse;
import com.android.dsly.rxhttp.observer.CommonObserver;
import com.android.dsly.rxhttp.utils.TransformerUtils;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import retrofit2.Response;

public class MainActivity extends RxAppCompatActivity implements IView {

    private LoadingDialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading_dialog = new LoadingDialog(this);
        RetrofitUrlManager.getInstance().putDomain("aaa", "https://api.apiopen.top/");
    }

    public void click1(View view) {
        RxHttp.createApi(CommonApi.class)
                .getNews()
                .compose(TransformerUtils.<String>pack(this, true))
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
                .compose(TransformerUtils.<String>packResp(this, true))
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
                .compose(TransformerUtils.<String>pack(this, true))
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
                .compose(TransformerUtils.<String>noCachePackResp(this, true))
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
                .compose(TransformerUtils.<String>cachePackResp(this, true,"click6", CacheMode.REQUEST_FAILED_READ_CACHE))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                        Log.e("aaa",response.getHeadersMap().toString());
                    }
                });
    }

    public void click7(View view){
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>cachePackResp(this, true,"click7", CacheMode.IF_NONE_CACHE_REQUEST))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("aaa",e.getMessage());
                    }
                });
    }

    public void click8(View view){
        RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>cachePackResp(this, true,"click8", CacheMode.FIRST_CACHE_THEN_REQUEST))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.isFromCache() + "  " + response.body());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("aaa",e.getMessage());
                    }
                });
    }

    public void click9(View view){
        RxHttp.createApi(CommonApi.class)
                .touTiao()
                .compose(TransformerUtils.<String>packResp(this,true))
                .subscribe(new CommonObserver<Response<String>>() {
                    @Override
                    protected void onSuccess(Response<String> response) {
                        Log.e("aaa", response.body());
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