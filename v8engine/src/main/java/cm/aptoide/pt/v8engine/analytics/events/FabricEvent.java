package cm.aptoide.pt.v8engine.analytics.events;

import cm.aptoide.pt.v8engine.analytics.Event;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import java.util.Map;
import java.util.Set;

/**
 * Created by trinkes on 30/06/2017.
 */

public class FabricEvent implements Event {
  private String name;
  private Map<String, String> data;
  private Answers fabric;

  public FabricEvent(Answers fabric, String name, Map<String, String> data) {
    this.name = name;
    this.data = data;
    this.fabric = fabric;
  }

  @Override public void send() {
    CustomEvent customEvent = new CustomEvent(name);
    Set<Map.Entry<String, String>> dataEntry = data.entrySet();

    for (Map.Entry<String, String> attribute : dataEntry) {
      customEvent.putCustomAttribute(attribute.getKey(), attribute.getValue());
    }

    fabric.logCustom(customEvent);
  }
}
