package cm.aptoide.analytics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

public class AnalyticsManagerTest {
  @Test public void sendSupportedEvent() {
    String eventName = "TestEvent";
    EventLogger eventLogger = Mockito.mock(EventLogger.class);
    KnockEventLogger knockEventLogger = Mockito.mock(KnockEventLogger.class);
    KeyValueNormalizer analyticsNormalizer = Mockito.mock(KeyValueNormalizer.class);
    AnalyticsLogger debugLogger = Mockito.mock(AnalyticsLogger.class);

    AnalyticsManager analyticsManager =
        new AnalyticsManager.Builder().addLogger(eventLogger, Arrays.asList(eventName))
            .setKnockLogger(knockEventLogger)
            .setAnalyticsNormalizer(analyticsNormalizer)
            .setDebugLogger(debugLogger)
            .build();

    Map<String, Object> data = new HashMap<>();
    AnalyticsManager.Action action = AnalyticsManager.Action.OPEN;
    String context = "timeline";
    analyticsManager.logEvent(data, eventName, action, context);
    Mockito.verify(eventLogger)
        .log(eventName, data, action, context);
  }

  @Test public void sendUnsupportedEvent() {
    String eventName = "TestEvent";
    EventLogger eventLogger = Mockito.mock(EventLogger.class);
    KnockEventLogger knockEventLogger = Mockito.mock(KnockEventLogger.class);
    KeyValueNormalizer analyticsNormalizer = Mockito.mock(KeyValueNormalizer.class);
    AnalyticsLogger debugLogger = Mockito.mock(AnalyticsLogger.class);

    AnalyticsManager analyticsManager =
        new AnalyticsManager.Builder().addLogger(eventLogger, Arrays.asList(eventName))
            .setKnockLogger(knockEventLogger)
            .setAnalyticsNormalizer(analyticsNormalizer)
            .setDebugLogger(debugLogger)
            .build();

    Map<String, Object> data = new HashMap<>();
    AnalyticsManager.Action action = AnalyticsManager.Action.OPEN;
    String context = "timeline";
    analyticsManager.logEvent(data, "Unsupported event", action, context);
    Mockito.verify(eventLogger, Mockito.times(0))
        .log(eventName, data, action, context);
  }
}