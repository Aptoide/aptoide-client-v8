package cm.aptoide.pt;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.DrawerActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 20-11-2017.
 */

public class UITests {

  /**
   * if it shows the next_icon button it means the user is opening Aptoide for the first time and it's on the Wizard
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
