package cm.aptoide.pt.blacklist;

public class BlacklistUnit {

  private final String id;
  private final int maxImpressions;

  public BlacklistUnit(String id, int maxImpressions) {
    this.id = id;
    this.maxImpressions = maxImpressions;
  }

  public String getId() {
    return id;
  }

  public int getMaxImpressions() {
    return maxImpressions;
  }
}
