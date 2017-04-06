/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PushNotificationsRequest extends V3<GetPushNotificationsResponse> {

  protected PushNotificationsRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static PushNotificationsRequest of(BodyInterceptor<BaseBody> bodyInterceptor) {
    BaseBody args = new BaseBody();

    String oemid = DataProvider.getConfiguration().getExtraId();
    if (!TextUtils.isEmpty(oemid)) {
      args.put("oem_id", oemid);
    }
    args.put("mode", "json");
    args.put("limit", "1");
    args.put("lang", AptoideUtils.SystemU.getCountryCode());

    if (BuildConfig.DEBUG || ManagerPreferences.isDebug()) {
      String notificationType = ManagerPreferences.getNotificationType();
      args.put("notification_type",
          TextUtils.isEmpty(notificationType) ? "aptoide_tests" : notificationType);
    } else {
      args.put("notification_type", "aptoide_vanilla");
    }
    args.put("id", String.valueOf(ManagerPreferences.getLastPushNotificationId()));
    return new PushNotificationsRequest(args, bodyInterceptor);
  }

  @Override
  protected Observable<GetPushNotificationsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPushNotifications(map, bypassCache);
  }
}
