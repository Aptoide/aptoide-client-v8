package cm.aptoide.pt;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.NUMBER_OF_RETRIES;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class)
public class DownloadsUITest {
  private final String LIGHT_APP_TO_SEARCH = "Cut the rope";
  private final String APP_TO_SEARCH = "Facebook";
  private int whileiterations = 0;
  private final int MAX_NUMBER_WHILEITERARTIONS = 45;
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);
  @Rule public RetryTestRule retry = new RetryTestRule(NUMBER_OF_RETRIES);


  @Before public void setUp() throws IOException, InterruptedException {
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  @Test public void download() {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToApp(APP_TO_SEARCH, 1);
    cancelIfDownloading();
    onView(withId(R.id.action_btn)).perform(swipeDown());
    onView(withId(R.id.action_btn)).check(matches(not(withText("OPEN"))));
    onView(withId(R.id.action_btn)).perform(click());
    closePopUp();
    allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    testIcActionButtons();
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.appview_button_install)));
  }

  @Test public void downloadAndInstall() {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    boolean isInstalling = false;
    goToApp(LIGHT_APP_TO_SEARCH, 2);
    cancelIfDownloading();
    onView(withId(R.id.action_btn)).perform(swipeDown());
    onView(withId(R.id.action_btn)).perform(click());
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
      } catch (Exception e) {
      }
    }
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.open)));
  }


  private boolean hasPermission(String permission) {
    Context context = InstrumentationRegistry.getTargetContext();
    String finalpermission = "android.permission." + permission;
    int permissionStatus = ContextCompat.checkSelfPermission(context, finalpermission);
    return (permissionStatus == PackageManager.PERMISSION_GRANTED);
  }

  private void closePopUp(){
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


  private void cancelIfDownloading() {
    try {
      onView(withId(R.id.ic_action_cancel)).perform(click());
    } catch (Exception e) {
    }
  }

  private void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  private void goToApp(String app, int pos) {
    onView(withId(R.id.action_search)).perform(click());
    onView(withId(R.id.search_src_text)).perform(replaceText(app), pressImeActionButton());
    onView(withId(R.id.all_stores_result_list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(pos, click()));
  }

  private void testIcActionButtons() {
    onView(withId(R.id.ic_action_pause)).perform(click());
    onView(withId(R.id.ic_action_resume)).perform(click());
    onView(withId(R.id.ic_action_cancel)).perform(click());
  }

  private void installApp(UiDevice mDevice) throws UiObjectNotFoundException {
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
    mDevice.findObject(new UiSelector().clickable(true)
        .checkable(false)
        .textContains("DONE"))
        .click();
  }

}
