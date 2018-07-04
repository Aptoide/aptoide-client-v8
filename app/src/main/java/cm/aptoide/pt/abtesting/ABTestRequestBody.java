package cm.aptoide.pt.abtesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestRequestBody {

  private List<Data> events;

  public ABTestRequestBody(String action) {
    events = new ArrayList<>();
    events.add(new Data(action));
  }

  public List<Data> getEvents() {
    return events;
  }

  public static class Data {
    private String name;

    Data(String action) {
      name = action;
    }

    public String getName() {
      return name;
    }
  }
}
