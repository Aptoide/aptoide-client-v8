/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PushNotificationsRequest extends V3<GetPushNotificationsResponse> {

  protected static String BASE_HOST =
      "http://ec2-54-171-208-126.eu-west-1.compute.amazonaws.com/pnp/v1/";

  private final Map<String, String> options;
  private final String id;

  protected PushNotificationsRequest(BaseBody baseBody, String id, Map<String, String> options,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(BASE_HOST, baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.options = options;
    this.id = id;
  }

  public static PushNotificationsRequest of(AptoideClientUUID aptoideClientUuid, String versionName,
      String appId) {

    Random r = new Random();
    int i1 = r.nextInt(100);

    String id = aptoideClientUuid.getUniqueIdentifier() + "_" + i1;

    Map<String, String> options = new HashMap<String, String>();

    // language (default = ‘en’)
    // aptoide_version, e.g. 8.1.2.0
    // oem_id
    // aptoide_package, e.g. cm.aptoide.pt

    options.put("language", AptoideUtils.SystemU.getCountryCode());
    options.put("aptoide_version", versionName);
    String oemid = DataProvider.getConfiguration().getExtraId();
    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);

    return new PushNotificationsRequest(null, id, options, null);
  }

  @Override
  protected Observable<GetPushNotificationsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    Observable<GetPushNotificationsResponse> pushNotificationsAmazon =
        interfaces.getPushNotificationsAmazon(id, options, true);
    return pushNotificationsAmazon;
  }
}
