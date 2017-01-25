package com.zireck.requestcache.library.network;

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface BaseApiService {
  @GET Call<ResponseBody> requestGet(@Url String url);

  @GET Call<ResponseBody> requestGet(@Url String url,
      @QueryMap(encoded = true) Map<String, String> query);
}