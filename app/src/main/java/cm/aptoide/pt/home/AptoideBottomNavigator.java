package cm.aptoide.pt.home;

/**
 * Created by D01 on 12/03/18.
 */

import rx.Observable;

/**
 * This interface is responsible for emiting the button clicks and navigating to the chosen fragment
 * if not the same that the user is currently in
 */
public interface AptoideBottomNavigator {

  /**
   * Emits the event of a click on the BottomNavigation
   */
  Observable<Integer> navigationEvent();

  /**
   * Shows the fragment in case the current Fragment is not the one that is selected
   */
  void showFragment(Integer menuItem);

  /**
   * Hides or shows the BottomNavigation depending on the fragment
   */
  void toogleBottomNavigation();

  /**
   * Puts the focus on the button of the BottomNavigation
   *
   * @param bottomNavigationItem Button to give focus
   */
  void setFocusOnBottomNavigationItem(int bottomNavigationItem);
}
