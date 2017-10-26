package cm.aptoide.pt.crashreports;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FabricEvent;
import com.crashlytics.android.answers.Answers;
import java.util.HashMap;
import java.util.Map;

// TODO remove this class as soon as the Fabric issue related to search attach is fixed.
@Deprecated public class IssuesAnalytics {
  private static final String SEARCH_ATTACH_ISSUE = "SEARCH_8511";
  private final Analytics analytics;
  private final Answers fabric;

  public IssuesAnalytics(Analytics analytics, Answers fabric) {
    this.analytics = analytics;
    this.fabric = fabric;
  }

  public void attachSearchFailed(boolean contextAndActivityAreNull) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("result",
        contextAndActivityAreNull ? "failed_null_parent" : "failed_invalid_state");
    analytics.sendEvent(new FabricEvent(fabric, SEARCH_ATTACH_ISSUE, attributes));
  }

  public void attachSearchSuccess(boolean usingContext) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("result", usingContext ? "success_context" : "success_activity");
    analytics.sendEvent(new FabricEvent(fabric, SEARCH_ATTACH_ISSUE, attributes));
  }
}
