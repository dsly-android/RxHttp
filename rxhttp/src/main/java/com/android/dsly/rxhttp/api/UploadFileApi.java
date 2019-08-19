package com.android.dsly.rxhttp.api;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * 文件上传
 *
 * @author dsly
 * @date 2019-08-13
 */
public interface UploadFileApi {

    /**
     * 上传多个文件
     *
     * @param uploadUrl 地址
     * @param files     文件
     * @return ResponseBody
     */
    @Multipart
    @POST
    Observable<String> uploadFiles(@Url String uploadUrl,
                                         @Part List<MultipartBody.Part> files);
}
