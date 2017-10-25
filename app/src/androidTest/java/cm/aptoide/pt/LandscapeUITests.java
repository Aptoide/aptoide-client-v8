package cm.aptoide.pt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import cm.aptoide.pt.view.MainActivity;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class)
public class LandscapeUITests {
  private final String APP_TO_SEARCH = "Facebook";
  private final int WAIT_TIME = 550;
  private final int LONGER_WAIT_TIME = 2000;
  private final int NUMBER_OF_RETRIES = 2;
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);
  //@Rule public RetryTestRule retry = new RetryTestRule(NUMBER_OF_RETRIES);

  private static ViewAction swipeRigthOnLeftMost() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT, Press.FINGER);
  }

  @Before public void setUp() throws IOException, InterruptedException {
    if (isFirstTime()) {
      skipWizard();
    }
    logOutorGoBack();
  }

  @Test public void landscapeHomeTab() throws InterruptedException {
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.stores)).perform(click());
    onView(withText(R.string.home_title)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.recycler_view)).perform(
        RecyclerViewActions.actionOnItemAtPosition(1, click()));
  }

  @Test public void landscapeSearch() throws InterruptedException {
    Activity activity = mActivityRule.getActivity();
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    onView(withId(R.id.all_stores_result_list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(0, click()));
  }

  @Test public void landscapeAppView() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    Activity activity = mActivityRule.getActivity();
    goToApp(APP_TO_SEARCH, 1);
    cancelIfDownloading();
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_share)).check(matches(isDisplayed()));
    onView(withId(R.id.app_icon)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).perform(click());
    Thread.sleep(LONGER_WAIT_TIME);
    closePopUp();
    allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_pause)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_resume)).perform(click());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.ic_action_cancel)).perform(click());
    onView(withId(R.id.menu_share)).check(matches(isDisplayed()));
    onView(withId(R.id.app_icon)).check(matches(isDisplayed()));
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
  }

  @Test public void landscapeSettings() throws InterruptedException {
    boolean checked;
    Activity activity = mActivityRule.getActivity();
    goToSettings();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    try {
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } catch (Exception e) {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    if (!checked) {
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } else {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    if (!checked) {
      onView(withText(R.string.yes)).perform(click());
    }
    Thread.sleep(LONGER_WAIT_TIME);
  }

  @Test public void landscapeWizard() throws InterruptedException {
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    preferences.edit().clear().apply();
    activity1.finish();
    mActivityRule.launchActivity(new Intent());
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    Thread.sleep(WAIT_TIME);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.skip_text)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.stores)).perform(click());
  }

  private boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean hasPermission(String permission) {
    Context context = InstrumentationRegistry.getTargetContext();
    String finalpermission = "android.permission." + permission;
    int permissionStatus = ContextCompat.checkSelfPermission(context, finalpermission);
    return (permissionStatus == PackageManager.PERMISSION_GRANTED);
  }

  private void closePopUp() throws InterruptedException {
    try {
      onView(withText("OK")).perform(click());
    } catch (NoMatchingViewException e) {
    }
  }

  private void allowPermission(UiDevice mDevice, String permission) {
    if (android.os.Build.VERSION.SDK_INT >= 23) {
      if (!hasPermission(permission)) {
        try {
          mDevice.findObject(new UiSelector().clickable(true)
              .checkable(false)
              .textContains("ALLOW"))
              .click();
        } catch (Exception e1) {
        }
      }
    }
  }

  private void closeIfIsNotLoggedInOnDownload() {
    try {
      onView(withId(R.id.not_logged_in_close)).perform(click());
    } catch (Exception e) {
    }
  }

  private void logOutorGoBack() {
    try {
      onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
      Thread.sleep(WAIT_TIME);
      onView(withId(R.id.profile_email_text)).perform(click());
      onView(withId(R.id.toolbar)).perform(swipeLeft());
      goToMyAccount();
      onView(withId(R.id.button_logout)).perform(click());
    } catch (Exception e) {
      onView(withId(R.id.toolbar)).perform(swipeLeft());
    }
  }

  private void cancelIfDownloading() {
    try {
      onView(withId(R.id.ic_action_cancel)).perform(click());
    } catch (Exception e) {
    }
  }

  private void goToMyAccount() throws InterruptedException {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.drawer_title_my_account)).perform(click());
  }

  private void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  private void goToApp(String app, int pos) throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(replaceText(app), pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(pos, click()));
  }

  private void barOnlySearchApp(String app) throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(replaceText(app));
  }

  private void goToSettings() throws InterruptedException {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.drawer_title_settings)).perform(click());
  }

}
