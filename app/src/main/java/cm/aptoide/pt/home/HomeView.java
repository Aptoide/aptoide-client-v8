package cm.aptoide.pt.home;

import java.util.List;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BottomNavigationFragment {
  void showHomeBundles(List<AppBundle> bundles);
}
