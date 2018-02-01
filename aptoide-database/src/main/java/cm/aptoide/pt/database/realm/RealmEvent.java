package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RealmEvent extends RealmObject {
  public static String PRIMARY_KEY_NAME = "timestamp";

  @PrimaryKey private long timestamp;
  private String eventName;
  private int action;
  private String context;
  private String data;

  public RealmEvent(long timestamp, String eventName, int action, String context, String data) {
    this.timestamp = timestamp;
    this.eventName = eventName;
    this.action = action;
    this.context = context;
    this.data = data;
  }

  public RealmEvent() {
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getEventName() {
    return eventName;
  }

  public int getAction() {
    return action;
  }

  public String getContext() {
    return context;
  }

  public String getData() {
    return data;
  }
}
