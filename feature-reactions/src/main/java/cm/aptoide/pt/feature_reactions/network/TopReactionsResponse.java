package cm.aptoide.pt.feature_reactions.network;

import java.util.List;

public class TopReactionsResponse {

  private int total;
  private My my;
  private List<ReactionTypeResponse> top;

  public TopReactionsResponse() {
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public My getMy() {
    return my;
  }

  public void setMy(My my) {
    this.my = my;
  }

  public List<ReactionTypeResponse> getTop() {
    return top;
  }

  public void setTop(List<ReactionTypeResponse> top) {
    this.top = top;
  }

  public static class My {
    private String type;
    private String uid;

    public My() {
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getUid() {
      return uid;
    }

    public void setUid(String uid) {
      this.uid = uid;
    }
  }

  public static class ReactionTypeResponse {

    private int total;
    private String type;

    public ReactionTypeResponse() {

    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
