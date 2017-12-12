package cm.aptoide.pt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToSettings;
import static cm.aptoide.pt.UITests.skipWizard;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class) public class SettingsUITests {
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() {
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  @Test public void matureTest() {
    TestType.types = TestType.TestTypes.MATURE;
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    boolean mature = preferences.getBoolean("matureChkBox", false);
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(withText(R.string.yes)).perform(click());
    } catch (NoMatchingViewException e) {
    }
    if (mature == preferences.getBoolean("matureChkBox", false)) {
      onView(withId(R.id.action_btn)).perform(click()); //if it's equal fail
    } else {
      mature = !mature;
    }
    pressBackButton();
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(withText(R.string.yes)).perform(click());
    } catch (NoMatchingViewException e) {
    }
    pressBackButton();
    if (mature == preferences.getBoolean("matureChkBox", false)) {
      onView(withId(R.id.action_btn)).perform(click()); //if it's equal fail
    }
  }

  private void pressBackButton() {
    ViewInteraction appCompatImageButton2 = onView(
        allOf(withContentDescription("Navigate up"), withParent(withId(R.id.toolbar)),
            isDisplayed()));
    appCompatImageButton2.perform(click());
  }
}
