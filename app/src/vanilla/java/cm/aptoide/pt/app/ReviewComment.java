package cm.aptoide.pt.app;

/**
 * Created by D01 on 22/05/2018.
 */

public class ReviewComment {
  private final String view;
  private final long total;

  public ReviewComment(String view, long total) {
    this.view = view;
    this.total = total;
  }

  public String getView() {
    return view;
  }

  public long getTotal() {
    return total;
  }
}
