package cm.aptoide.pt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import androidx.preference.PreferenceManager;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.TestType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.goToSettings;
import static cm.aptoide.pt.UITests.skipWizard;

@RunWith(AndroidJUnit4.class) public class LandscapeUITests {

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
    TestType.initialization = TestType.TestTypes.REGULAR;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  /**
   * Switch screen orientation to landscape. Perform some action. Switch back again. Perform some
   * action
   */
  @Test public void landscapeHomeTab() {
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.toolbar)).perform(swipeUp());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    onView(withId(R.id.toolbar)).perform(swipeUp());
  }

  /**
   * Navigate to settings, press any checkbox there. switch to landscape. press checkbox again to
   * see if it has the different result
   */
  @Test public void landscapeSettings() {
    boolean checked;
    Activity activity = mActivityRule.getActivity();
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(withText(R.string.yes)).perform(click());
      checked =
          true; //if it doesn't fail it means that the checkbox was unchecked and is now checked
    } catch (Exception e) {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    if (!checked) { //it has the different behaviour than last time
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } else {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    if (!checked) {
      onView(withText(R.string.yes)).perform(click());
    }
  }

  /**
   * First time opening Aptoide. Switch to landscape. Navigate to HomeFragment
   */
  @Test public void landscapeWizard() {
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    preferences.edit()
        .clear()
        .apply();
    activity1.finish();
    mActivityRule.launchActivity(new Intent());
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  /**
   * User LoggedIn. Navigate to My Account. Presses Edit Profile. Modify name. Switch to landscape.
   * Check if the change is still there
   */
  @Test public void landscapeEditProfileName() {
    TestType.initialization = TestType.TestTypes.LOGGEDIN;
    goToMyAccount();
    Activity activity = mActivityRule.getActivity();
    onView(withId(R.id.create_user_username_inserted)).perform(click());
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("D011"),
        closeSoftKeyboard());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.create_user_username_inserted)).check(matches(withText("D011")));
  }

  /**
   * <p>Mock User LoggedIn.</p>
   * Navigate to My Account. Presses Edit Store. Modify store description. Switch to
   * landscape. Check if the change is still there
   */
  @Test public void landscapeEditStoreDescription() {
    TestType.initialization = TestType.TestTypes.LOGGEDINWITHSTORE;
    goToMyAccount();
    SystemClock.sleep(5000);
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    onView(withId(R.id.edit_store_description)).perform(click());
    Activity activity = mActivityRule.getActivity();
    onView(withId(R.id.edit_store_description)).perform(replaceText("D011"), closeSoftKeyboard());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.edit_store_description)).check(matches(withText("D011")));
  }
}
