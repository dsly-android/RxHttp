package com.android.dsly.rxhttp.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author 陈志鹏
 * @date 2020/12/18
 */
public interface DownloadFileApi {

    //@Streaming：直接返回一个流，而不将文件保存到内存中
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}