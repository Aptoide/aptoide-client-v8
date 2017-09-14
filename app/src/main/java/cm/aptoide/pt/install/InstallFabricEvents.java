package cm.aptoide.pt.install;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FabricEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/06/2017.
 */

public class InstallFabricEvents implements InstallerAnalytics {
  public static final String ROOT_V2_COMPLETE = "Root_v2_Complete";
  public static final String ROOT_V2_START = "Root_v2_Start";
  private final AppEventsLogger facebook;
  private Analytics analytics;
  private Answers fabric;

  public InstallFabricEvents(Analytics analytics, Answers fabric, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.fabric = fabric;
    this.facebook = facebook;
  }

  @Override public void rootInstallCompleted(int exitcode) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "success");
    attributes.put("Exit_Code", String.valueOf(exitcode));
    analytics.sendEvent(new FabricEvent(fabric, ROOT_V2_COMPLETE, attributes));
  }

  @Override public void rootInstallTimeout() {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "timeout");
    analytics.sendEvent(new FabricEvent(fabric, ROOT_V2_COMPLETE, attributes));
  }

  @Override public void rootInstallFail(Exception e) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "fail");
    attributes.put("Error", e.getMessage());
    analytics.sendEvent(new FabricEvent(fabric, ROOT_V2_COMPLETE, attributes));
  }

  @Override public void rootInstallCancelled() {
    Map<String, String> attributes = new HashMap<>();
    Bundle bundle = new Bundle();
    bundle.putString("Result", "cancel");
    attributes.put("Result", "cancel");
    analytics.sendEvent(new FabricEvent(fabric, ROOT_V2_COMPLETE, attributes));
    analytics.sendEvent(new FacebookEvent(facebook, ROOT_V2_COMPLETE, bundle));
  }

  @Override public void rootInstallStart() {
    analytics.sendEvent(new FabricEvent(fabric, ROOT_V2_START));
    analytics.sendEvent(new FacebookEvent(facebook, ROOT_V2_START));
  }
}
