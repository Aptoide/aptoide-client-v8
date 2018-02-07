package cm.aptoide.pt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.TestType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.goToSettings;
import static cm.aptoide.pt.UITests.skipWizard;

@RunWith(AndroidJUnit4.class) public class LandscapeUITests {

  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() {
    TestType.types = TestType.TestTypes.REGULAR;
    TestType.initialization = TestType.TestTypes.REGULAR;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  @Test public void landscapeHomeTab() {
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.toolbar)).perform(swipeUp());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    onView(withId(R.id.toolbar)).perform(swipeUp());
  }

  @Test public void landscapeSettings() {
    boolean checked;
    Activity activity = mActivityRule.getActivity();
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    try {
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } catch (Exception e) {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    if (!checked) {
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
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.skip_text)).perform(click());
  }

  @Test public void landscapeEditProfileName() {
    TestType.initialization = TestType.TestTypes.LOGGEDIN;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    Activity activity = mActivityRule.getActivity();
    onView(withId(R.id.create_user_username_inserted)).perform(click());
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("D011"),
        closeSoftKeyboard());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.create_user_username_inserted)).check(matches(withText("D011")));
  }

  @Test public void landscapeEditStoreDescription() {
    TestType.initialization = TestType.TestTypes.LOGGEDINWITHSTORE;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    Activity activity = mActivityRule.getActivity();
    onView(withId(R.id.edit_store_description)).perform(click());
    onView(withId(R.id.edit_store_description)).perform(replaceText("D011"), closeSoftKeyboard());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    onView(withId(R.id.edit_store_description)).check(matches(withText("D011")));
  }
}
