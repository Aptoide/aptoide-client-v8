/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheInterceptor;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by neuro on 22-03-2016.
 */
abstract class Aptwords<U> extends WebService<Aptwords.Interfaces, U> {

  private static final String BASE_URL = BuildConfig.APTOIDE_WEB_SERVICES_APTWORDS_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_APTWORDS_HOST
      + "/api/2/";

  Aptwords(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(Interfaces.class, httpClient, converterFactory, BASE_URL);
  }

  interface Interfaces {

    @POST("getAds") @FormUrlEncoded Observable<GetAdsResponse> getAds(
        @FieldMap HashMapNotNull<String, String> arg,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("registerAdReferer") @FormUrlEncoded
    Observable<RegisterAdRefererRequest.DefaultResponse> load(
        @FieldMap HashMapNotNull<String, String> arg);
  }
}
