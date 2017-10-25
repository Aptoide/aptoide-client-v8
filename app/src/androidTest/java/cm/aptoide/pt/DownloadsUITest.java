package cm.aptoide.pt;

import android.content.Context;
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
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.content.ContextCompat;
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
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class)
public class DownloadsUITest {
  private final String LIGHT_APP_TO_SEARCH = "Cut the rope";
  private final String APP_TO_SEARCH = "Facebook";
  private final int WAIT_TIME = 550;
  private final int LONGER_WAIT_TIME = 2000;
  private final int NUMBER_OF_RETRIES = 2;
  private final int MAX_NUMBER_WHILEITERARTIONS = 45;
  private int whileiterations = 0;
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

  @Test public void download() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToApp(APP_TO_SEARCH, 1);
    cancelIfDownloading();
    while (!hasOpenedAppView()&& whileiterations < MAX_NUMBER_WHILEITERARTIONS) {
      whileiterations++;
      Thread.sleep(WAIT_TIME);
    }
    onView(withId(R.id.action_btn)).check(matches(not(withText("OPEN"))));
    onView(withId(R.id.action_btn)).perform(click());
    Thread.sleep(LONGER_WAIT_TIME);
    closePopUp();
    allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    testIcActionButtons();
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.appview_button_install)));
  }

  @Test public void downloadAndInstall() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    boolean isInstalling = false;
    int cyclecount = 0;
    goToApp(LIGHT_APP_TO_SEARCH, 2);
    cancelIfDownloading();
    while (!hasOpenedAppView()&& whileiterations < MAX_NUMBER_WHILEITERARTIONS) {
      whileiterations++;
      Thread.sleep(WAIT_TIME);
    }
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.install)));
    onView(withId(R.id.action_btn)).perform(click());
    Thread.sleep(LONGER_WAIT_TIME);
    try {
      installApp(mDevice);
      isInstalling = true;
    } catch(Exception e){

    }
    if(!isInstalling) {
      closePopUp();
      allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
      closeIfIsNotLoggedInOnDownload();
    }
    while(!isInstalling && whileiterations < MAX_NUMBER_WHILEITERARTIONS * 3) {
      whileiterations++;
      try {
        installApp(mDevice);
        isInstalling = true;
        Thread.sleep(LONGER_WAIT_TIME);
      } catch (Exception e) {
        Thread.sleep(LONGER_WAIT_TIME);
      }
    }
    Thread.sleep(LONGER_WAIT_TIME*3);
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.open)));
  }

  private boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean hasOpenedAppView() {
    try {
      onView(withId(R.id.action_btn)).perform(swipeDown());
      return true;
    } catch (Exception e) {
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

  private void testIcActionButtons() throws InterruptedException {
    onView(withId(R.id.ic_action_pause)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_resume)).perform(click());
    onView(withId(R.id.ic_action_cancel)).perform(click());
  }

  private void installApp(UiDevice mDevice) throws UiObjectNotFoundException, InterruptedException {
    try {
      mDevice.findObject(new UiSelector().clickable(true)
          .checkable(false)
          .textContains("Next"))
          .click();
    } catch (Exception e1) {
    }
    mDevice.findObject(new UiSelector().clickable(true)
        .checkable(false)
        .textContains("INSTALL"))
        .click();
    Thread.sleep(LONGER_WAIT_TIME);
    mDevice.findObject(new UiSelector().clickable(true)
        .checkable(false)
        .textContains("DONE"))
        .click();
  }

}
