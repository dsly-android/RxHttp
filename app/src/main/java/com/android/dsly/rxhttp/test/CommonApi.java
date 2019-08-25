/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.dsly.rxhttp.test;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CommonApi {

    @GET("journalismApi")
    Observable<String> getNews();

    @Headers({"Domain-Name:aaa"})
    @GET("getJoke")
    Observable<Response<String>> getJoke();

    @GET("https://is.snssdk.com/api/news/feed/v78/?fp=LlTqLrFbLzmtFlPqL2U1FYFILlK7&version_code=6.9.0&app_name=news_article_lite&vid=21173F1A-315F-42D1-9997-9F611F0052C0&device_id=47139265055&channel=App%20Store&resolution=750*1334&aid=35&ab_version=668904,668906,668903,679106,668905,933995,643995,661935,785656,668907,846821,861726,1118982,928942&ab_feature=201615,z1&review_flag=0&ab_group=z1,201615&update_version_code=6900&openudid=207fbb8511d4c11d260b38556408b59ddbe47fd5&pos=5pe9vb%252F%252B9Onkv72nvb94Ezt0CjW%252FsZe9vb%252Fx8vP69Ono%252Bfi%252Fvae9rKyls6yrpK6kq6ipq6quqquqsZe9vb%252Fx%252FOn06ej5%252BL%252B9p72vqbOppKWvpaqqpKmsqKqrrKSX4A%253D%253D&idfv=21173F1A-315F-42D1-9997-9F611F0052C0&ac=WIFI&os_version=12.3.1&ssmix=a&device_platform=iphone&iid=82657810766&ab_client=a1,f2,f7,e1&device_type=iPhone%208&idfa=0D627B16-0792-4BB6-B936-5130703D51C0&language=zh-Hans-CN&image=1&list_count=26&count=20&tt_from=pull&latitude=24.49828779415762&city=%E5%8E%A6%E9%97%A8&last_refresh_sub_entrance_interval=1566716173&loc_time=1566716079&refer=1&refresh_reason=1&concern_id=6286225228934679042&longitude=118.1693965467377&session_refresh_idx=4&strict=0&LBS_status=authroize&detail=1&min_behot_time=1566716139&loc_mode=1&cp=58Dd682d3a10Eq1")
    Observable<Response<String>> touTiao();
}
