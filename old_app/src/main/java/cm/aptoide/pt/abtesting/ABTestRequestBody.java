package cm.aptoide.pt.abtesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestRequestBody {

  private final List<Data> events;

  public ABTestRequestBody(String action) {
    events = new ArrayList<>();
    events.add(new Data(action));
  }

  public List<Data> getEvents() {
    return events;
  }

  public static class Data {
    private final String name;

    Data(String action) {
      name = action;
    }

    public String getName() {
      return name;
    }
  }
}
