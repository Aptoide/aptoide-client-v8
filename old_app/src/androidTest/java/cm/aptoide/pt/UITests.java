package cm.aptoide.pt;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * Created by jose_messejana on 20-11-2017.
 */

public class UITests {

  /**
   * if it shows the next_icon button it means the user is opening Aptoide for the first time and
   * it's on the Wizard
   *
   * @return
   */
  protected static boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  protected static void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  /**
   * Opens drawer and navigates to Settings
   */
  protected static void goToSettings() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withText(R.string.drawer_title_settings)).perform(click());
  }

  /**
   * Opens drawer and navigates to My Account
   */
  protected static void goToMyAccount() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withText(R.string.drawer_title_my_account)).perform(click());
  }
}
