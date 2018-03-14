package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;

/**
 * Created by D01 on 12/03/18.
 */


/*
All fragments from the bottomNavigation should extend this interface
 */

public interface BottomNavigationFragment extends View {

  /*
  The fragments for the bottomNavigation should be able to scroll to beginning of the screen when choosen when the fragment already exists
   */
  void scrollToTop();
}
