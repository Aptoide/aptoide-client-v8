package cm.aptoide.pt.notification;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.notifications.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.notifications.PullCampaignNotificationsRequest;
import cm.aptoide.pt.dataprovider.ws.notifications.PullSocialNotificationRequest;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.IdsRepository;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

public class NotificationHandler implements NotificationNetworkService {
  private final PublishRelay<AptoideNotification> handler;
  private final String applicationId;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final IdsRepository idsRepository;
  private final String versionName;
  private final AuthenticationPersistence authenticationPersistence;
  private final AptoideAccountManager accountManager;
  private final String extraId;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;

  public NotificationHandler(String applicationId, OkHttpClient httpClient,
      Converter.Factory converterFactory, IdsRepository idsRepository, String versionName,
      String extraId, PublishRelay<AptoideNotification> relay, SharedPreferences sharedPreferences,
      Resources resources, AuthenticationPersistence authenticationPersistence,
      AptoideAccountManager accountManager) {
    this.applicationId = applicationId;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.idsRepository = idsRepository;
    this.versionName = versionName;
    this.authenticationPersistence = authenticationPersistence;
    this.handler = relay;
    this.extraId = extraId;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.accountManager = accountManager;
  }

  @Override public Single<List<AptoideNotification>> getSocialNotifications() {
    return authenticationPersistence.getAuthentication()
        .flatMapObservable(
            authentication -> PullSocialNotificationRequest.of(idsRepository.getUniqueIdentifier(),
                versionName, applicationId, httpClient, converterFactory, extraId,
                authentication.getAccessToken(), sharedPreferences, resources)
                .observe())
        .flatMap(response -> accountManager.accountStatus()
            .first()
            .map(account -> convertSocialNotifications(response, account.getId())))
        .flatMap(notifications -> handle(notifications))
        .toSingle();
  }

  @Override public Single<List<AptoideNotification>> getCampaignNotifications() {
    return PullCampaignNotificationsRequest.of(idsRepository.getUniqueIdentifier(), versionName,
        applicationId, httpClient, converterFactory, extraId, sharedPreferences, resources)
        .observe()
        .flatMap(response -> accountManager.accountStatus()
            .first()
            .map(account -> convertCampaignNotifications(response, account.getId())))
        .first()
        .flatMap(notifications -> handle(notifications))
        .toSingle();
  }

  @NonNull private Observable<List<AptoideNotification>> handle(
      List<AptoideNotification> aptoideNotifications) {
    return Observable.from(aptoideNotifications)
        .doOnNext(notification -> handler.call(notification))
        .toList();
  }

  public Observable<AptoideNotification> getHandlerNotifications() {
    return handler;
  }

  private List<AptoideNotification> convertSocialNotifications(
      List<GetPullNotificationsResponse> response, String id) {
    List<AptoideNotification> aptoideNotifications = new LinkedList<>();
    for (final GetPullNotificationsResponse notification : response) {
      String appName = null;
      String graphic = null;
      if (notification.getAttr() != null) {
        appName = notification.getAttr()
            .getAppName();
        graphic = notification.getAttr()
            .getAppGraphic();
      }

      aptoideNotifications.add(
          new AptoideNotification(notification.getBody(), notification.getImg(),
              notification.getTitle(), notification.getUrl(), notification.getType(), appName,
              graphic, AptoideNotification.NOT_DISMISSED, id, notification.getExpire(),
              notification.getUrlTrack(), notification.getUrlTrackNc()));
    }
    return aptoideNotifications;
  }

  private List<AptoideNotification> convertCampaignNotifications(
      List<GetPullNotificationsResponse> response, String id) {
    List<AptoideNotification> aptoideNotifications = new LinkedList<>();
    for (final GetPullNotificationsResponse notification : response) {
      String appName = null;
      String graphic = null;
      if (notification.getAttr() != null) {
        appName = notification.getAttr()
            .getAppName();
        graphic = notification.getAttr()
            .getAppGraphic();
      }

      aptoideNotifications.add(
          new AptoideNotification(notification.getAbTestingGroup(), notification.getBody(),
              notification.getCampaignId(), notification.getImg(), notification.getLang(),
              notification.getTitle(), notification.getUrl(), notification.getUrlTrack(), appName,
              graphic, id, notification.getExpire(), notification.getUrlTrackNc()));
    }
    return aptoideNotifications;
  }
}
