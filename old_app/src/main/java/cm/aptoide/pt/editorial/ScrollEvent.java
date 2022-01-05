package cm.aptoide.pt.editorial;

/**
 * Created by D01 on 18/09/2018.
 */

public class ScrollEvent {
  private final Boolean isItemShown;

  public ScrollEvent(Boolean isItemShown) {
    this.isItemShown = isItemShown;
  }

  public Boolean getItemShown() {
    return isItemShown;
  }
}
