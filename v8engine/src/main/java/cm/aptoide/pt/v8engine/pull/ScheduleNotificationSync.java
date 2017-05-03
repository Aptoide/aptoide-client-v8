package cm.aptoide.pt.v8engine.pull;

import android.content.Context;
import android.content.SyncResult;
import cm.aptoide.pt.dataprovider.ws.notifications.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.notifications.PullCampaignNotificationsRequest;
import cm.aptoide.pt.dataprovider.ws.notifications.PullSocialNotificationRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.sync.ScheduledSync;
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

  public ScheduleNotificationSync(IdsRepository idsRepository, Context context,
      OkHttpClient httpClient, Converter.Factory converterFactory, String versionName,
      String applicationId) {
    this.idsRepository = idsRepository;
    this.context = context;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.versionName = versionName;
    this.applicationId = applicationId;
    subscriptions = new CompositeSubscription();
  }

  @Override public void sync(SyncResult syncResult) {

    PullCampaignNotificationsRequest.of(idsRepository.getUniqueIdentifier(), versionName,
        applicationId, httpClient, converterFactory).execute(response -> saveData(response));

    subscriptions.add(
        (((V8Engine) context.getApplicationContext()).getAccountManager()).accountStatus()
            .first()
            .filter(account -> account.isLoggedIn())
            .subscribe(
                packageInfo -> PullSocialNotificationRequest.of(idsRepository.getUniqueIdentifier(),
                    versionName, applicationId, httpClient, converterFactory)
                    .execute(response -> saveData(response))));
  }

  private void saveData(List<GetPullNotificationsResponse> response) {
    Logger.d(TAG, "sync: saving data\n" + response);
  }
}
