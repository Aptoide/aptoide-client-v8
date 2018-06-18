package cm.aptoide.pt.abtesting;

/**
 * Created by franciscocalado on 15/06/18.
 */

public class Experiment {
  private static final long TWENTY_FOUR_HOURS = 864000000;

  private long requestTime;
  private String name;
  private String assignment;
  private String payload;

  public Experiment(String name, String payload, String assignment) {
    this.requestTime = System.currentTimeMillis();
    this.name = name;
    this.assignment = assignment;
    this.payload = payload;
  }

  public boolean isExpired() {
    return requestTime < (System.currentTimeMillis() - TWENTY_FOUR_HOURS);
  }

  public String getName() {
    return name;
  }

  public String getAssignment() {
    return assignment;
  }

  public String getPayload() {
    return payload;
  }
}
