package cm.aptoide.pt.v8engine.pull;

import android.content.SyncResult;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.sync.ScheduledSync;

/**
 * Created by trinkes on 02/05/2017.
 */

public class ScheduleNotificationSync extends ScheduledSync {
  private static final String TAG = ScheduleNotificationSync.class.getSimpleName();


  public ScheduleNotificationSync() {
  }

  @Override public void sync(SyncResult syncResult) {


    Logger.d(TAG, "sync: syncing");
  }
}
