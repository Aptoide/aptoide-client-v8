package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import io.rakam.api.Identify;
import io.rakam.api.Rakam;

/**
 * Created by trinkes on 28/09/2017.
 */

public class AptoideApplicationAnalytics {

  private final String APTOIDE_PACKAGE = "aptoide_package";

  public void updateDimension(boolean isLoggedIn) {
    Bundle bundle = new Bundle();
    bundle.putString("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
  }

  public void setPackageDimension(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString(APTOIDE_PACKAGE, packageName);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty(APTOIDE_PACKAGE, packageName);

    if (BuildConfig.FLAVOR_mode.equals("dev")) {
      Rakam.getInstance()
          .identify(new Identify().add(APTOIDE_PACKAGE, packageName));
    }
  }

  public void setVersionCodeDimension(String versionCode) {
    Bundle bundle = new Bundle();
    bundle.putString("version code", versionCode);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("version code", versionCode);
  }
}
