package cm.aptoide.pt;

import android.app.Activity;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.TestType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static cm.aptoide.pt.UITests.goToSettings;
import static cm.aptoide.pt.UITests.skipWizard;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class) public class SettingsUITests {

  /**
   * Sets up the activity in which each test opens
   */
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  /**
   * <p>Sets up which mocks to "activate"</p>
   * Skips Wizards in case it's the first time opening aptoide
   */
  @Before public void setUp() {
    TestType.types = TestType.TestTypes.REGULAR;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  /**
   * <p>Checks if Mature is turned On, after leaving the settings view.</p>
   * Navigate to Settings. Press Mature Checkbox. Navigate to HomeFragment. Navigate to Settings.
   * Press Mature Checkbox
   */
  @Test public void matureTest() {
    TestType.types = TestType.TestTypes.MATURE;
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    boolean mature = preferences.getBoolean("matureChkBox", false);
    goToSettings();
    onView(ViewMatchers.withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(ViewMatchers.withId(R.id.list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(ViewMatchers.withText(R.string.yes)).perform(click());
    } catch (NoMatchingViewException e) {
    }
    pressBackButton();
    goToSettings();
    onView(ViewMatchers.withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(ViewMatchers.withId(R.id.list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(ViewMatchers.withText(R.string.yes)).perform(click());
    } catch (NoMatchingViewException e) {
    }
    pressBackButton();
    if (mature != preferences.getBoolean("matureChkBox", false)) {
      onView(ViewMatchers.withId(R.id.action_btn)).perform(
          click()); //if it's equal it means the button was not checked/unchecked
    }
  }

  private void pressBackButton() {
    ViewInteraction appCompatImageButton2 = onView(
        allOf(withContentDescription("Navigate up"), withParent(ViewMatchers.withId(R.id.toolbar)),
            isDisplayed()));
    appCompatImageButton2.perform(click());
  }
}
