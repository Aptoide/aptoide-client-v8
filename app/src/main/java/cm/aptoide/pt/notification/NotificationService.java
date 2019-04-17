package cm.aptoide.pt.notification;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v1.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.v1.notification.PullCampaignNotificationsRequest;
import cm.aptoide.pt.networking.IdsRepository;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class NotificationService {
  private final String applicationId;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final IdsRepository idsRepository;
  private final String versionName;
  private final AptoideAccountManager accountManager;
  private final String extraId;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;

  public NotificationService(String applicationId, OkHttpClient httpClient,
      Converter.Factory converterFactory, IdsRepository idsRepository, String versionName,
      String extraId, SharedPreferences sharedPreferences, Resources resources,
      AptoideAccountManager accountManager) {
    this.applicationId = applicationId;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.idsRepository = idsRepository;
    this.versionName = versionName;
    this.extraId = extraId;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.accountManager = accountManager;
  }

  public Single<List<AptoideNotification>> getCampaignNotifications() {
    return PullCampaignNotificationsRequest.of(idsRepository.getUniqueIdentifier(), versionName,
        applicationId, httpClient, converterFactory, extraId, sharedPreferences, resources)
        .observe()
        .flatMap(response -> accountManager.accountStatus()
            .first()
            .map(account -> convertCampaignNotifications(response, account.getId())))
        .first()
        .toSingle();
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
          new AptoideNotification(notification.getBody(), notification.getImg(),
              notification.getTitle(), notification.getUrl(), AptoideNotification.CAMPAIGN, appName,
              graphic, AptoideNotification.NOT_DISMISSED, id, notification.getUrlTrack(),
              notification.getUrlTrackNc(), false, System.currentTimeMillis(),
              notification.getExpire(), notification.getAbTestingGroup(),
              notification.getCampaignId(), notification.getLang()));
    }
    return aptoideNotifications;
  }
}
