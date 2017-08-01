package cm.aptoide.pt.install;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FabricEvent;
import com.crashlytics.android.answers.Answers;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/06/2017.
 */

public class InstallFabricEvents implements InstallerAnalytics {
  public static final String EVENT_NAME = "Root_v2";
  private Analytics analytics;
  private Answers fabric;

  public InstallFabricEvents(Analytics analytics, Answers fabric) {
    this.analytics = analytics;
    this.fabric = fabric;
  }

  @Override public void rootInstallCompleted(int exitcode) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "success");
    attributes.put("Exit_Code", String.valueOf(exitcode));
    analytics.sendEvent(new FabricEvent(fabric, "Root_v2", attributes));
  }

  @Override public void rootInstallTimeout() {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "timeout");
    analytics.sendEvent(new FabricEvent(fabric, "Root_v2", attributes));
  }

  @Override public void rootInstallFail(Exception e) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Result", "fail");
    attributes.put("Error", e.getMessage());
    analytics.sendEvent(new FabricEvent(fabric, EVENT_NAME, attributes));
  }
}
