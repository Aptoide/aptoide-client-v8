package cm.aptoide.pt.abtesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestActionBody {
  private List<Data> events;

  public ABTestActionBody(String assignment) {
    events = new ArrayList<>();
    events.add(new Data(assignment));
  }

  public List<Data> getEvents() {
    return events;
  }

  public void setEvents(List<Data> events) {
    this.events = events;
  }

  public static class Data {
    private String name;

    Data(String assignment) {
      name = assignment;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
