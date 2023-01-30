package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import io.rakam.api.Rakam;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trinkes on 28/09/2017.
 */

public class AptoideApplicationAnalytics {

  public static final String IS_ANDROID_TV = "Is_Android_Tv";
  private static final String CONTEXT = "APPLICATION";
  private static final String APTOIDE_PACKAGE = "aptoide_package";
  private static final String IS_ANDROID_TV_FIELD = "is_android_tv";
  private final AnalyticsManager analyticsManager;

  public AptoideApplicationAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void updateDimension(boolean isLoggedIn) {
    Bundle bundle = new Bundle();
    bundle.putString("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.UserProperties.add("Logged In", isLoggedIn ? "Logged In" : "Not Logged In");
    Rakam.getInstance()
        .setSuperProperties(addJsonLoginSuperProperty(isLoggedIn, Rakam.getInstance()
            .getSuperProperties()));
    //Indicative.addProperty("logged_in", isLoggedIn);
  }

  @NotNull
  private JSONObject addJsonLoginSuperProperty(boolean isLoggedIn, JSONObject superProperties) {
    if (superProperties == null) {
      superProperties = new JSONObject();
    }
    try {
      superProperties.put("logged_in", isLoggedIn);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return superProperties;
  }

  public void setPackageDimension(String packageName) {
    Bundle bundle = new Bundle();
    bundle.putString(APTOIDE_PACKAGE, packageName);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.UserProperties.add(APTOIDE_PACKAGE, packageName);
  }

  public void setVersionCodeDimension(String versionCode) {
    Bundle bundle = new Bundle();
    bundle.putString("version code", versionCode);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.UserProperties.add("version code", versionCode);
  }

  public void sendIsTvEvent(boolean isTv) {
    Map<String, Object> data = new HashMap<>();
    data.put(IS_ANDROID_TV_FIELD, isTv);

    analyticsManager.logEvent(data, IS_ANDROID_TV, AnalyticsManager.Action.AUTO, CONTEXT);
  }
}
