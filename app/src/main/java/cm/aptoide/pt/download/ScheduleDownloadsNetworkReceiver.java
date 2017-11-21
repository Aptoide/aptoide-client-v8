package cm.aptoide.pt.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.download.view.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;

/**
 * Created by trinkes on 9/29/16.
 */

public class ScheduleDownloadsNetworkReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {

    int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
    final boolean wifiEnabled = (extraWifiState == WifiManager.WIFI_STATE_ENABLED);

    if (!wifiEnabled) {
      // connectivity change detected but wifi is not enabled
      // does nothing
      return;
    }

    final boolean scheduledDownloadsEnabled = ManagerPreferences.scheduledDownloadsEnabled(
        ((AptoideApplication) context.getApplicationContext()).getDefaultSharedPreferences());

    if (!scheduledDownloadsEnabled) {
      // scheduled downloads auto-start is not enabled
      // does nothing
      return;
    }

    // start scheduled downloads (if there are any)
    if (RepositoryFactory.getScheduledDownloadRepository(context.getApplicationContext())
        .hasScheduleDownloads()) {
      Intent i = new Intent(Intent.ACTION_VIEW,
          Uri.parse(ScheduledDownloadsFragment.OPEN_SCHEDULE_DOWNLOADS_WITH_POPUP_URI));
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }
}
