package cm.aptoide.pt.v8engine.notification;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.notifications.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.notifications.PullCampaignNotificationsRequest;
import cm.aptoide.pt.dataprovider.ws.notifications.PullSocialNotificationRequest;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationHandler implements NotificationNetworkService {
  private final PublishRelay<AptoideNotification> handler;
  private String applicationId;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private IdsRepository idsRepository;
  private String versionName;
  private AptoideAccountManager accountManager;

  public NotificationHandler(String applicationId, OkHttpClient httpClient,
      Converter.Factory converterFactory, IdsRepository idsRepository, String versionName,
      AptoideAccountManager accountManager) {
    this.applicationId = applicationId;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.idsRepository = idsRepository;
    this.versionName = versionName;
    this.accountManager = accountManager;
    handler = PublishRelay.create();
  }

  @Override public Single<List<AptoideNotification>> getSocialNotifications() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> PullSocialNotificationRequest.of(idsRepository.getUniqueIdentifier(),
            versionName, applicationId, httpClient, converterFactory,
            DataProvider.getConfiguration()
                .getExtraId(), account.getAccessToken())
            .observe()
            .map(response -> convertSocialNotifications(response, account.getId())))
        .flatMap(notifications -> handle(notifications))
        .toSingle();
  }

  @Override public Single<List<AptoideNotification>> getCampaignNotifications() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> PullCampaignNotificationsRequest.of(idsRepository.getUniqueIdentifier(),
            versionName, applicationId, httpClient, converterFactory)
            .observe()
            .map(response -> convertCampaignNotifications(response, account.getId()))
            .first()
            .flatMap(notifications -> handle(notifications)))
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
              graphic, -1, id));
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
              graphic, id));
    }
    return aptoideNotifications;
  }
}
