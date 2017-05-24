/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.notifications;

import android.support.annotation.NonNull;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.networkclient.WebService;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by sithengineer on 21/07/16.
 */
public abstract class Notifications<U> extends WebService<Notifications.Interfaces, U> {

  protected static final String BASE_HOST = "http://pnp.aptoide.com/pnp/v1/notifications/";

  protected Notifications(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(Interfaces.class, httpClient, converterFactory, BASE_HOST);
  }

  @NonNull public static String getErrorMessage(BaseV3Response response) {
    final StringBuilder builder = new StringBuilder();
    if (response != null && response.getErrors() != null) {
      for (ErrorResponse error : response.getErrors()) {
        builder.append(error.msg);
        builder.append(". ");
      }
      if (builder.length() == 0) {
        builder.append("Server failed with empty error list.");
      }
    } else {
      builder.append("Server returned null response.");
    }
    return builder.toString();
  }

  @Override public Observable<U> observe(boolean bypassCache) {

    return super.observe(bypassCache);
  }

  interface Interfaces {

    @GET("{id}/campaigns")
    Observable<List<GetPullNotificationsResponse>> getPullCompaignNotifications(
        @Path("id") String id, @QueryMap Map<String, String> options,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @GET("{id}/direct") Observable<List<GetPullNotificationsResponse>> getPullSocialNotifications(
        @Path("id") String id, @QueryMap Map<String, String> options,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);
  }
}
