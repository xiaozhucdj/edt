package com.yougy.plide;


import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by FH on 2018/1/12.
 */

public interface DownloadRetrofitApi {

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
