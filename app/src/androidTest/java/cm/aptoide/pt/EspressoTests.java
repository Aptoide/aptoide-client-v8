package cm.aptoide.pt;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)

@LargeTest public class EspressoTests {

  private final String SIGNUPEMAILTESTBGN = "jose.messejana+";
  private final String SIGNUPEMAILTESTEND = "@aptoide.com";
  private final String LOGINEMAIL = "jose.messejana@aptoide.com";
  private final String PASS = "aptoide1234";
  private final String APP_TO_SEARCH = "Cut the Rope";
  private final int WAIT_TIME = 550;
  private final int LONGER_WAIT_TIME = 2000;
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);
  private boolean isFirst = true;
  private String SIGNUPEMAILTEST = "";

  private static ViewAction swipeRigthOnLeftMost() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT, Press.FINGER);
  }

  public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
    return new TypeSafeMatcher<View>() {
      int currentIndex = 0;

      @Override public void describeTo(Description description) {
        description.appendText("with index: ");
        description.appendValue(index);
        matcher.describeTo(description);
      }

      @Override public boolean matchesSafely(View view) {
        return matcher.matches(view) && currentIndex++ == index;
      }
    };
  }

  @Before public void setUpEmail() {
    if (isFirst) {
      SIGNUPEMAILTEST = SIGNUPEMAILTESTBGN + System.currentTimeMillis() + SIGNUPEMAILTESTEND;
      isFirst = false;
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
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    complete_sign_up();
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
    while (!hasLoggedIn()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    checkIfLoggedIn();
    goToMyAccount();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void download() throws InterruptedException {
    if (isFirstTime()) {
      skipWizard();
    }
    logOutorGoBack();
    goToApp();
    cancelIfDownloading();
    while (!hasOpenedAppView()) {
      Thread.sleep(WAIT_TIME);
    }
    closePopUp();
    closeIfIsNotLoggedInOnDownload();
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    Thread.sleep(WAIT_TIME);
    testIc_ActionButtons();
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.appview_button_install)));
  }

  //Boolean Methods

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

  private boolean hasLoggedIn() {
    try {
      onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean notSignUp() {
    try {
      onView(withId(R.id.create_user_username_inserted)).check(matches(isDisplayed()));
      return false;
    } catch (NoMatchingViewException e) {
      return true;
    }
  }

  private boolean hasCreatedUser() {
    try {
      onView(withId(R.id.logged_in_continue)).perform(click());
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean hasCreatedStore() {
    try {
      onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean hasOpenedAppView() {
    try {
      onView(withId(R.id.action_btn)).perform(click());
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  // IfElse Actions

  private void closePopUp() throws InterruptedException {
    try {
      onView(withText("OK")).perform(click());
      //onView(withText("ALLOW")).perform(click()); doesn't work needs to give permissions before initiating tests android 7+
    } catch (NoMatchingViewException e) {
      try {
        onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
      } catch (NoMatchingViewException e1) {
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

  //Refactored Methods

  private void goToMyAccount() throws InterruptedException {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    onView(withText(R.string.my_account_title_my_account)).perform(click());
  }

  private void performLogin() throws InterruptedException {
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(LOGINEMAIL));
    onView(withId(R.id.password)).perform(replaceText(PASS), closeSoftKeyboard());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp() throws InterruptedException {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(SIGNUPEMAILTEST));
    onView(withId(R.id.password)).perform(replaceText(PASS), closeSoftKeyboard());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_sign_up)).perform(click());
  }

  private void checkIfLoggedIn() {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    onView(withId(R.id.profile_email_text)).check(matches(isDisplayed()));
    onView(withId(R.id.profile_email_text)).check(matches(withText(LOGINEMAIL)));
    onView(withId(R.id.profile_name_text)).check(matches(isDisplayed()));
    onView(withId(R.id.toolbar)).perform(swipeLeft());
  }

  private void skipWizard() {
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.next_icon)).perform(click());
    onView(withId(R.id.skip_text)).perform(click());
  }

  private void goToApp() throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(replaceText(APP_TO_SEARCH),
        pressImeActionButton());
    Thread.sleep(2 * WAIT_TIME);
    onView(withIndex(withId(R.id.recycler_view), 0)).perform(
        RecyclerViewActions.actionOnItemAtPosition(1, click()));
  }

  private void testIc_ActionButtons() {
    onView(withId(R.id.ic_action_pause)).perform(click());
    onView(withId(R.id.ic_action_resume)).perform(click());
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    onView(withId(R.id.ic_action_cancel)).perform(click());
  }

  private void complete_sign_up() throws InterruptedException {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    while (!hasCreatedUser()) {
      Thread.sleep(WAIT_TIME);
    }
    while (!hasCreatedStore()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_skip)).perform(click());
  }
}
