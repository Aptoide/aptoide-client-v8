package cm.aptoide.pt;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import cm.aptoide.pt.view.MainActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBackUnconditionally;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)

@LargeTest public class EspressoTests {

  private final String EMAILTESTBGN = "jose.messejana";
  private final String EMAILTESTEND = "@aptoide.com";
  private String EMAILTEST = "";
  private final String PASS = "aptoide1234";
  private final String APP_TO_SEARCH = "Cut the Rope";
  private final int WAIT_TIME = 550;
  private boolean isFirst = true;

  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  private static ViewAction swipeRigthOnLeftMost() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT, Press.FINGER);
  }

  public static ViewAction clickXY(final int x, final int y) {
    return new GeneralClickAction(Tap.SINGLE, new CoordinatesProvider() {
      @Override public float[] calculateCoordinates(View view) {

        final int[] screenPos = new int[2];
        view.getLocationOnScreen(screenPos);

        final float screenX = screenPos[0] + x;
        final float screenY = screenPos[1] + y;
        float[] coordinates = { screenX, screenY };

        return coordinates;
      }
    }, Press.FINGER);
  }

  public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
    return new TypeSafeMatcher<View>() {
      int currentIndex = 0;

      @Override
      public void describeTo(Description description) {
        description.appendText("with index: ");
        description.appendValue(index);
        matcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        return matcher.matches(view) && currentIndex++ == index;
      }
    };
  }

  @Before public void setUpEmail() {
    if(isFirst) {
        EMAILTEST = EMAILTESTBGN + System.currentTimeMillis() + EMAILTESTEND;
        isFirst= false;
    }
  }

  @Test public void signUp() throws InterruptedException {
    if (isFirstTime()) {
      onView(withId(R.id.next_icon)).perform(click());
      onView(withId(R.id.next_icon)).perform(click());
      performSignUp();
    } else {
      goToMyAccount();
      if (!isLoggedIn()) {
        performSignUp();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        goToMyAccount();
        performSignUp();
      }
    }
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  @Test public void signIn() throws InterruptedException {
    if (isFirstTime()) {
      onView(withId(R.id.next_icon)).perform(click());
      onView(withId(R.id.next_icon)).perform(click());
      performLogin();
    } else {
      goToMyAccount();
      if (!isLoggedIn()) {
        performLogin();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        goToMyAccount();
        performLogin();
      }
    }
    goToMyAccount();
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void download() throws InterruptedException {
    if (isFirstTime()) {
      skipWizard();
    }
    goToApp();
    cancelIfDownloading();
    onView(withId(R.id.action_btn)).perform(click());
    closePopUp();
    closeIfIsNotLoggedInOnDownload();
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_pause)).perform(click());
    onView(withId(R.id.ic_action_resume)).perform(click());
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    onView(withId(R.id.ic_action_cancel)).perform(click());
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.appview_button_install)));
  }

  private boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean isLoggedIn() {
    try {
      onView(withId(R.id.show_login_with_aptoide_area)).check(matches(isDisplayed()));
      return false;
    } catch (NoMatchingViewException e) {
      return true;
    }
  }

  private boolean closeIfIsNotLoggedInOnDownload() {
    try {
      onView(withId(R.id.not_logged_in_close)).perform(click());
      return false;
    } catch (Exception e) {
      return true;
    }
  }

  private boolean isDownloading() {
    try {
      onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isInstalling() {
    try {
      onView(withText("Installing...")).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private void closePopUp() {
    try {
      onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    } catch (NoMatchingViewException e) {
      pressBackUnconditionally();
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
    onView(withText(R.string.my_account_title_my_account)).perform(
        click());//  TO DO Should work with navigation_item_my_account
  }

  private void performLogin() throws InterruptedException {
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(EMAILTEST));
    onView(withId(R.id.password)).perform(replaceText(PASS), closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp() throws InterruptedException {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(EMAILTEST));
    onView(withId(R.id.password)).perform(replaceText(PASS), closeSoftKeyboard());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_sign_up)).perform(click());
  }

  private void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  private void goToApp() throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    onView(withId(R.id.search_src_text)).perform(replaceText(APP_TO_SEARCH),
        pressImeActionButton());
    onView(withIndex(withId(R.id.recycler_view), 0)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
  }
}
