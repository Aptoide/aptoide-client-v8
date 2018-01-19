package cm.aptoide.pt.install;

import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/06/2017.
 */

public class InstallFabricEvents implements InstallerAnalytics {
  public static final String ROOT_V2_COMPLETE = "Root_v2_Complete";
  public static final String ROOT_V2_START = "Root_v2_Start";
  private static final String INSTALLFABRICEVENT = "Install_Fabric_Event";
  private AnalyticsManager analyticsManager;

  public InstallFabricEvents(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  @Override public void rootInstallCompleted(int exitcode) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "success");
    attributes.put("Exit_Code", String.valueOf(exitcode));
    analyticsManager.logEvent(attributes,ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT, INSTALLFABRICEVENT);
  }

  @Override public void rootInstallTimeout() {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "timeout");
    analyticsManager.logEvent(attributes,ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,INSTALLFABRICEVENT);
  }

  @Override public void rootInstallFail(Exception e) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "fail");
    attributes.put("Error", e.getMessage());
    analyticsManager.logEvent(attributes,ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,INSTALLFABRICEVENT);
  }

  @Override public void rootInstallCancelled() {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("Result", "cancel");
    analyticsManager.logEvent(attributes,ROOT_V2_COMPLETE, AnalyticsManager.Action.ROOT,INSTALLFABRICEVENT);
  }

  @Override public void rootInstallStart() {
    analyticsManager.logEvent(new HashMap<>(),ROOT_V2_START, AnalyticsManager.Action.ROOT,INSTALLFABRICEVENT);
  }
}
