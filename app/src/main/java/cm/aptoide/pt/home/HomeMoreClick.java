package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 12/03/2018.
 */

public class HomeMoreClick {
  private final HomeBundle bundle;
  private final int position;

  public HomeMoreClick(HomeBundle bundle, int position) {
    this.bundle = bundle;
    this.position = position;
  }

  public HomeBundle getBundle() {
    return bundle;
  }

  public int getPosition() {
    return position;
  }
}
