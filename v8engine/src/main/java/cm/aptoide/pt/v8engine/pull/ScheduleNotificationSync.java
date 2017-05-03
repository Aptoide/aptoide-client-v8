package cm.aptoide.pt.v8engine.pull;

import android.content.Context;
import android.content.SyncResult;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.dataprovider.ws.notifications.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.notifications.PullCampaignNotificationsRequest;
import cm.aptoide.pt.dataprovider.ws.notifications.PullSocialNotificationRequest;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.sync.ScheduledSync;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 02/05/2017.
 */

public class ScheduleNotificationSync extends ScheduledSync {
  private static final String TAG = ScheduleNotificationSync.class.getSimpleName();
  private final CompositeSubscription subscriptions;
  private IdsRepository idsRepository;
  private Context context;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private String versionName;
  private String applicationId;
  private NotificationAccessor notificationAccessor;

  public ScheduleNotificationSync(IdsRepository idsRepository, Context context,
      OkHttpClient httpClient, Converter.Factory converterFactory, String versionName,
      String applicationId, NotificationAccessor notificationAccessor) {
    this.idsRepository = idsRepository;
    this.context = context;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.versionName = versionName;
    this.applicationId = applicationId;
    this.notificationAccessor = notificationAccessor;
    subscriptions = new CompositeSubscription();
  }

  @Override public void sync(SyncResult syncResult) {

    PullCampaignNotificationsRequest.of(idsRepository.getUniqueIdentifier(), versionName,
        applicationId, httpClient, converterFactory)
        .execute(response -> saveCampaignNotifications(response));

    subscriptions.add(
        (((V8Engine) context.getApplicationContext()).getAccountManager()).accountStatus()
            .first()
            .filter(account -> account.isLoggedIn())
            .subscribe(
                packageInfo -> PullSocialNotificationRequest.of(idsRepository.getUniqueIdentifier(),
                    versionName, applicationId, httpClient, converterFactory)
                    .execute(response -> saveSocialNotifications(response))));
  }

  private void saveSocialNotifications(List<GetPullNotificationsResponse> response) {
    LinkedList<Notification> notifications = new LinkedList<>();
    for (final GetPullNotificationsResponse notification : response) {
      notifications.add(
          new Notification(notification.getBody(), notification.getImg(), notification.getTitle(),
              notification.getUrl()));
    }
    saveData(notifications);
  }

  private void saveCampaignNotifications(List<GetPullNotificationsResponse> response) {
    LinkedList<Notification> notifications = new LinkedList<>();
    for (final GetPullNotificationsResponse notification : response) {
      notifications.add(new Notification(notification.getAbTestingGroup(), notification.getBody(),
          notification.getCampaignId(), notification.getImg(), notification.getLang(),
          notification.getTitle(), notification.getUrl(), notification.getUrlTrack()));
    }
    saveData(notifications);
  }

  private void saveData(List<Notification> response) {
    notificationAccessor.insertAll(response);
  }
}
