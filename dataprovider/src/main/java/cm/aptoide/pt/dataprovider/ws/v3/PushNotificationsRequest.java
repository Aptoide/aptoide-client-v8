/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PushNotificationsRequest extends V3<GetPushNotificationsResponse> {

  protected PushNotificationsRequest(BaseBody baseBody) {
    super(BASE_HOST, baseBody);
  }

  public static PushNotificationsRequest of() {
    BaseBody args = new BaseBody();

    String oemid = DataProvider.getConfiguration().getExtraId();
    if (!TextUtils.isEmpty(oemid)) {
      args.put("oem_id", oemid);
    }
    args.put("mode", "json");
    args.put("limit", "1");
    //args.put("lang", AptoideUtils.SystemU.getCountryCode());
    args.put("lang", ManagerPreferences.getLanguage());

    if (BuildConfig.DEBUG || ManagerPreferences.isDebug()) {
      String notificationType = ManagerPreferences.getNotificationType();
      args.put("notification_type",
          TextUtils.isEmpty(notificationType) ? "aptoide_tests" : notificationType);
    } else {
      args.put("notification_type", "aptoide_vanilla");
    }
    args.put("id", String.valueOf(ManagerPreferences.getLastPushNotificationId()));
    args.put("aptoide_vercode", Integer.toString(AptoideUtils.Core.getVerCode()));
    return new PushNotificationsRequest(args);
  }

  @Override
  protected Observable<GetPushNotificationsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPushNotifications(map, bypassCache);
  }
}
