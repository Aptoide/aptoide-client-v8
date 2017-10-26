package cm.aptoide.pt.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.v3.PushNotificationsRequest;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import rx.Single;

public class NotificationService {

  private final String extraId;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final Context context;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final AptoideAccountManager accountManager;

  public NotificationService(String extraId, SharedPreferences sharedPreferences,
      Resources resources, Context context, TokenInvalidator tokenInvalidator,
      BodyInterceptor<BaseBody> bodyInterceptorV3, AptoideAccountManager accountManager) {
    this.extraId = extraId;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.context = context;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.accountManager = accountManager;
  }

  public Single<List<AptoideNotification>> getPushNotifications(int lastPushNotificationId) {
    return PushNotificationsRequest.of(sharedPreferences, extraId, bodyInterceptorV3,
        tokenInvalidator, AptoideUtils.Core.getVerCode(context), lastPushNotificationId,
        AptoideUtils.SystemU.getCountryCode(resources))
        .observe()
        .flatMap(response -> accountManager.accountStatus()
            .first()
            .map(account -> convertPushNotifications(response, account.getId())))
        .toSingle();
  }

  private List<AptoideNotification> convertPushNotifications(GetPushNotificationsResponse response,
      String ownerId) {
    List<AptoideNotification> notifications = new LinkedList<>();
    for (final GetPushNotificationsResponse.Notification notificationResponse : response.getResults()) {
      ManagerPreferences.setLastPushNotificationId(notificationResponse.getId(), sharedPreferences);

      notifications.add(new AptoideNotification(notificationResponse.getImages()
          .getIconUrl(), notificationResponse.getTitle(), notificationResponse.getTargetUrl(),
          notificationResponse.getTrackUrl(), notificationResponse.getImages()
          .getBannerUrl(), AptoideNotification.CAMPAIGN, notificationResponse.getId(),
          System.currentTimeMillis(), ownerId));
    }
    return notifications;
  }
}
