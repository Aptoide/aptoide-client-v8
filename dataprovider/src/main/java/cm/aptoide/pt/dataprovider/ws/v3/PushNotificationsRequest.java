package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import rx.Observable;

/**
 * Created by danielchen on 03/10/2017.
 */

public class PushNotificationsRequest extends V3<GetPushNotificationsResponse> {

  protected PushNotificationsRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(BASE_HOST, baseBody, bodyInterceptor, tokenInvalidator);
  }

  public static PushNotificationsRequest of(SharedPreferences sharedPreferences, String oemId,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      int applicationVersionCode, int lastPushNotificationId, String countryCode) {
    BaseBody args = new BaseBody();

    args.put("oem_id", oemId);
    args.put("mode", "json");
    args.put("limit", "1");
    args.put("lang", countryCode);

    if (BuildConfig.DEBUG || ManagerPreferences.isDebug(sharedPreferences)) {
      String notificationType = ManagerPreferences.getNotificationType(sharedPreferences);
      args.put("notification_type",
          TextUtils.isEmpty(notificationType) ? "aptoide_tests" : notificationType);
    } else {
      args.put("notification_type", "aptoide_vanilla");
    }
    args.put("id", String.valueOf(lastPushNotificationId));
    args.put("aptoide_vercode", Integer.toString(applicationVersionCode));
    return new PushNotificationsRequest(args, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<GetPushNotificationsResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getPushNotifications(map, bypassCache);
  }
}