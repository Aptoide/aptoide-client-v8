package cm.aptoide.pt.app.view;

/**
 * Created by D01 on 18/09/2018.
 */

class ScrollEvent {
  private final boolean scrollDown;
  private final Boolean isItemShown;

  public ScrollEvent(boolean scrollDown, Boolean isItemShown) {
    this.scrollDown = scrollDown;
    this.isItemShown = isItemShown;
  }

  public boolean isScrollDown() {
    return scrollDown;
  }

  public Boolean getItemShown() {
    return isItemShown;
  }
}
