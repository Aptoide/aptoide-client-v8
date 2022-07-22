package cm.aptoide.pt.feature_reactions.data;

public class TopReaction {

  private final String type;
  private final int total;

  public TopReaction(String type, int total) {
    this.type = type;
    this.total = total;
  }

  public String getType() {
    return type;
  }

  public int getTotal() {
    return total;
  }
}
