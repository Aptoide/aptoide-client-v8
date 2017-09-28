package cm.aptoide.pt;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTests {

  private final String EMAILTEST2 = "Jose.Messejana@aptoide.com";
  private final String PASS = "passwordteste0123";
  private boolean isLoggedIn = true;
  private boolean isFirstTime = true;
  private final String myAccountbuttonString = "My Account";

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

  @Test public void signUp() throws InterruptedException {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
    } catch(NoMatchingViewException e){
      isFirstTime = false;
    }
    if(isFirstTime){
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.next_icon)).perform(click());
      performSignUp();
    }
    else{
      Thread.sleep(1000);
      onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
      Thread.sleep(2000);
      onView(withText(myAccountbuttonString)).perform(click());// Should work with navigation_item_my_account
      Thread.sleep(500);
      try { //user not logged in
        onView(withText(R.id.show_login_with_aptoide_area)).check(matches(isDisplayed()));
      } catch (NoMatchingViewException e) {
        isLoggedIn = false;
      }
      Thread.sleep(500);
      if (!isLoggedIn) {
        performSignUp();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        //TO DO
      }
    }
  }

  @Test public void signIn() throws InterruptedException {
      try {
        onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      } catch(NoMatchingViewException e){
        isFirstTime = false;
      }
      if(isFirstTime)
        onView(withId(R.id.next_icon)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.next_icon)).perform(click());
        performLogin();
      }
      else{
      Thread.sleep(1000);
      onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
      Thread.sleep(2000);
      onView(withText(myAccountbuttonString)).perform(click());// Should work with navigation_item_my_account
      Thread.sleep(500);
      try { //user not logged in
        onView(withText(R.id.show_login_with_aptoide_area)).check(matches(isDisplayed()));
      } catch (NoMatchingViewException e) {
        isLoggedIn = false;
      }
      Thread.sleep(500);
      if (!isLoggedIn) {
        performLogin();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        //TO DO
      }
    }
  }

  @Test public void download() throws InterruptedException {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
    } catch(NoMatchingViewException e){
      isFirstTime = false;
    }
    if(isFirstTime){
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.skip_text)).perform(click());
    }
  }

  private void performLogin() throws InterruptedException {
    Thread.sleep(500);
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.username)).perform(replaceText(EMAILTEST2));
    onView(withId(R.id.password)).perform(replaceText(PASS),closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp() throws InterruptedException {
    Thread.sleep(500);
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.username)).perform(replaceText(EMAILTEST2));
    onView(withId(R.id.password)).perform(replaceText(PASS),closeSoftKeyboard() );
    onView(withId(R.id.button_sign_up)).perform(click());
    //TO DO PROFILE FLOW
    //TO DO Logout user after registration
  }

  private static ViewAction swipeRigthOnLeftMost(){
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT, Press.FINGER);
  }
}
