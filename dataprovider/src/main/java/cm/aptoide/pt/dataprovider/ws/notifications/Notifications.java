/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.notifications;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by on 21/07/16.
 */
public abstract class Notifications<U> extends WebService<Service, U> {

  protected Notifications(OkHttpClient httpClient, Converter.Factory converterFactory,
      SharedPreferences sharedPreferences) {
    super(Service.class, httpClient, converterFactory, getHost(sharedPreferences));
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_NOTIFICATION_HOST
        + "/pnp/v1/notifications/";
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
}
