package cm.aptoide.pt.v8engine.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.fragment.implementations.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.ScheduledDownloadRepository;

import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by trinkes on 9/29/16.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    handleScheduledDownloads(context, intent);
  }

  private void handleScheduledDownloads(@NonNull final Context context,
      @NonNull final Intent intent) {
    if (intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1) != TYPE_WIFI
        || !AptoideUtils.NetworkUtils.isAvailable(context, TYPE_WIFI)) {
      return;
    }
    if (!ManagerPreferences.isScheduleDownloadsEnable()
        || !AptoideUtils.NetworkUtils.isGeneralDownloadPermitted(context,
        ManagerPreferences.getGeneralDownloadsWifi(),
        ManagerPreferences.getGeneralDownloadsMobile())) {
      return;
    }

    ScheduledDownloadRepository scheduledRepository =
        RepositoryFactory.getRepositoryFor(Scheduled.class);
    if (scheduledRepository != null && scheduledRepository.hasScheduleDownloads()) {
      Intent i = new Intent(Intent.ACTION_VIEW,
          Uri.parse(ScheduledDownloadsFragment.OPEN_SCHEDULE_DOWNLOADS_WITH_POPUP_URI));
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }
}
