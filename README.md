导入：<br>  
implementation 'com.android.dsly:rxhttp:1.0.0'

正常使用：
```java
RxHttp.createApi(CommonApi.class)
                .getNews()
                .compose(TransformerUtils.<String>pack(this, true))
                .subscribe(new CommonObserver<String>() {
                    @Override
                    protected void onSuccess(String s) {
                        Log.e("aaa", s);
                    }
                });
```
多baseurl：https://github.com/JessYanCoding/RetrofitUrlManager

上传文件：
```java
RxHttp.uploadFile("http://t.xinhuo.com/index.php/Api/Pic/uploadPic",
                "", "/sdcard/yunk/yunkImg/5cc7b2a9ab22e.jpg")
                .compose(TransformerUtils.<String>pack(this, true))
                .subscribe(new CommonObserver<String>() {
                    @Override
                    protected void onSuccess(String s) {
                        Log.e("aaa", s);
                    }
                });
```
不使用缓存：
```java
RxHttp.createApi(CommonApi.class)
                .getJoke()
                .compose(TransformerUtils.<String>noCachePackResp(this, true))
                .subscribe(new CommonObserver<RxHttpResponse<String>>() {
                    @Override
                    protected void onSuccess(RxHttpResponse<String> response) {
                        Log.e("aaa", response.body());
                    }
                });
```
先使用缓存，不管是否存在，仍然请求网络:
```java
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
```