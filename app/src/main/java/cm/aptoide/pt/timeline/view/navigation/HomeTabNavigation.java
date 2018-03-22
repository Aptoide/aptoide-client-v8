package cm.aptoide.pt.timeline.view.navigation;

import android.os.Bundle;
import cm.aptoide.pt.navigator.TabNavigation;

/**
 * Created by jdandrade on 02/05/2017.
 */

public class HomeTabNavigation implements TabNavigation {

  public HomeTabNavigation() {
  }

  @Override public Bundle getBundle() {
    return new Bundle();
  }

  @Override public int getTab() {
    return HOME;
  }
}
