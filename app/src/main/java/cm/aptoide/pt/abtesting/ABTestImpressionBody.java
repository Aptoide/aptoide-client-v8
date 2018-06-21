package cm.aptoide.pt.abtesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestImpressionBody {

  private List<Data> events;

  public ABTestImpressionBody() {
    events = new ArrayList<>();
    events.add(new Data());
  }

  public List<Data> getEvents() {
    return events;
  }

  public static class Data {
    private String name;

    Data() {
      name = "IMPRESSION";
    }

    public String getName() {
      return name;
    }
  }
}
