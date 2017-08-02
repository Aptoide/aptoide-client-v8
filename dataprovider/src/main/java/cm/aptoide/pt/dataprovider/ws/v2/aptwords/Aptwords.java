/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
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

  Aptwords(OkHttpClient httpClient, Converter.Factory converterFactory,
      SharedPreferences sharedPreferences) {
    super(Interfaces.class, httpClient, converterFactory, getHost(sharedPreferences));
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_APTWORDS_HOST
        + "/api/2/";
  }

  interface Interfaces {

    @POST("getAds") @FormUrlEncoded Observable<GetAdsResponse> getAds(
        @FieldMap HashMapNotNull<String, String> arg,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("registerAdReferer") @FormUrlEncoded
    Observable<RegisterAdRefererRequest.DefaultResponse> load(
        @FieldMap HashMapNotNull<String, String> arg);
  }
}
