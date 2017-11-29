package cm.aptoide.pt.social.analytics;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FabricEvent;
import com.crashlytics.android.answers.Answers;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by franciscocalado on 11/29/17.
 */

public class TimelineGameAnalytics {
  private final Analytics analytics;
  private final Answers fabric;

  public TimelineGameAnalytics(Analytics analytics, Answers fabric) {
    this.analytics = analytics;
    this.fabric = fabric;
  }

  public void cardMappingFailed(String cardType) {
    Map<String, String> result = new HashMap<>();
    result.put("result", "Bad mapping of card type " + cardType);
    analytics.sendEvent(new FabricEvent(fabric, cardType + "_CARD", result));
  }

  public void cardMappingSucceeded(String cardType) {
    Map<String, String> result = new HashMap<>();
    result.put("result", cardType + " card successfully mapped");
    analytics.sendEvent(new FabricEvent(fabric, cardType + "_CARD", result));
  }
}
