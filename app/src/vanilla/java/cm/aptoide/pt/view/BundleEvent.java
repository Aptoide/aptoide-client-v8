package cm.aptoide.pt.view;

/**
 * Created by D01 on 05/06/2018.
 */

public class BundleEvent {
  private final String title;
  private final String action;

  public BundleEvent(String title, String action) {
    this.title = title;
    this.action = action;
  }

  public String getTitle() {
    return title;
  }

  public String getAction() {
    return action;
  }
}
