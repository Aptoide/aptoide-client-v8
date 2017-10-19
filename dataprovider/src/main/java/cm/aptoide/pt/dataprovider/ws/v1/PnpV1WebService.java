/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v1;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.WebService;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public abstract class PnpV1WebService<U> extends WebService<Service, U> {

  protected PnpV1WebService(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(Service.class, httpClient, converterFactory, getHost());
  }

  public static String getHost() {
    return "http://" + BuildConfig.APTOIDE_WEB_SERVICES_NOTIFICATION_HOST + "/pnp/v1/";
  }
}
