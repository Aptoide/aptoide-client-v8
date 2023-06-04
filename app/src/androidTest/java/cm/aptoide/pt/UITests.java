package cm.aptoide.pt;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 20-11-2017.
 */

public class UITests {

  /**
   * Checks if it shows the next_icon button, indicating that the user is opening Aptoide for the first time and
   * it's on the Wizard.
   *
   * @return true if it's the first time, false otherwise.
   */
  protected static boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  /**
   * Skips the Wizard by performing the necessary clicks.
   */
  protected static void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  /**
   * Opens the drawer and navigates to the Settings screen.
   */
  protected static void goToSettings() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withText(R.string.drawer_title_settings)).perform(click());
  }

  /**
   * Opens the drawer and navigates to the My Account screen.
   */
  protected static void goToMyAccount() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withText(R.string.drawer_title_my_account)).perform(click());
  }
}
