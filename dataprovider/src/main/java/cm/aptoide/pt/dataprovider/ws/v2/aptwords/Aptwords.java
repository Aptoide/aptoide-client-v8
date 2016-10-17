/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import okhttp3.OkHttpClient;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by neuro on 22-03-2016.
 */
abstract class Aptwords<U> extends WebService<Aptwords.Interfaces, U> {

  private static final String BASE_URL = "http://webservices.aptwords.net/api/2/";

  Aptwords() {
    super(Interfaces.class,
        OkHttpClientFactory.getSingletonClient(SecurePreferences.getUserAgent()),
        WebService.getDefaultConverter(), BASE_URL);
  }

  Aptwords(OkHttpClient httpClient) {
    super(Interfaces.class, httpClient, WebService.getDefaultConverter(), BASE_URL);
  }

  interface Interfaces {

    @POST("getAds") @FormUrlEncoded Observable<GetAdsResponse> getAds(
        @FieldMap HashMapNotNull<String, String> arg);

    @POST("registerAdReferer") @FormUrlEncoded
    Observable<RegisterAdRefererRequest.DefaultResponse> load(
        @FieldMap HashMapNotNull<String, String> arg);
  }
}
