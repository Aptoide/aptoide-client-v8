package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;

/**
 * Created by trinkes on 28/09/2017.
 */

public class AptoideApplicationAnalytics {

  public void updateDimension(boolean isLoggedIn) {
    Bundle bundle = new Bundle();
    bundle.putString("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
  }

  public void setPackageDimension(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString("aptoide_package", packageName);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("aptoide_package", packageName);

  }

  public void setVersionCodeDimension(String versionCode) {
    Bundle bundle = new Bundle();
    bundle.putString("version code", versionCode);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("version code", versionCode);
  }
}
