package cm.aptoide.pt.shareappsandroid;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.lite.localytics.AnalyticsLite;
//import cm.aptoide.lite.localytics.LocalyticsSession;

/**
 * Created by filipegoncalves on 01-02-2017.
 */

public class AnalyticsManager {

  private Context context;
  private Intent intent;

  public AnalyticsManager(Context context, Intent intent) {
    this.context = context;
    this.intent = intent;
  }

  /**
   * Setting up Localytics to analyse app usage data
   */
  public void generateLocalyticsSettings() {

    //AnalyticsLite.localyticsSession = new LocalyticsSession(context);
    //AnalyticsLite.localyticsSession.setCustomDimension(AnalyticsLite.LITE_CUSTOM_DIMENSION_VERTICAL, AnalyticsLite.LITE_CUSTOM_DIMENSION);
    //        DataHolder.getInstance().setLocalyticsSession(mLocalyticsSession);
    this.resumeLocalytics();
  }

  /**
   * This will open localytics session and upload Localytics data for this session's API key. This
   * should be done early in the process life.
   */
  private void resumeLocalytics() {
    //        AnalyticsLite.localyticsSession.open();
    //        AnalyticsLite.localyticsSession.handlePushReceived(intent);
    //        AnalyticsLite.localyticsSession.upload();
  }

  public void joinGroupSuccess() {
    AnalyticsLite.groupClick("Join Group", "Success");
  }

  public void joinGroupUnsuccess() {
    AnalyticsLite.groupClick("Join Group", "Unsuccessful");
  }

  public void createGroupUnsuccess() {
    AnalyticsLite.groupClick("Create Group", "Unsuccessful");
  }

  public void createGroupSuccess() {
    AnalyticsLite.groupClick("Create Group", "Success");
  }
}
