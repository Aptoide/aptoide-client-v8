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
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
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
@LargeTest
public class EspressoTests {

  private final String EMAILTEST2 = "Jose.Messejana@aptoide.com";
  private final String PASS = "passwordteste0123";
  private final int XCOORDINATE = 144;
  private final int YCOORDINATE = 144;

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

  @Test public void signUp() throws InterruptedException {
    if(isFirstTime()){
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.next_icon)).perform(click());
      performSignUp();
    }
    else{
      goToMyAccount();
      Thread.sleep(500);
      if (!isLoggedIn()) {
        performSignUp();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        goToMyAccount();
        performSignUp();
      }
    }
  }

  @Test public void signIn() throws InterruptedException {
      if(isFirstTime()){
        onView(withId(R.id.next_icon)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.next_icon)).perform(click());
        performLogin();
      }
      else{
        goToMyAccount();
      Thread.sleep(500);
      if (!isLoggedIn()) {
        performLogin();
      } else {
        onView(withId(R.id.button_logout)).perform(click());
        goToMyAccount();
        performLogin();
      }
    }
  }

  @Test public void download() throws InterruptedException {
    if(isFirstTime()){
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.next_icon)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.skip_text)).perform(click());
    }
    else{
      Thread.sleep(500);
      onView(withId(R.id.action_search)).perform(click());
      Thread.sleep(500);
      onView(withId(R.id.search_src_text)).perform(replaceText("Cut the rope"), pressImeActionButton());
      Thread.sleep(1000);
      onView(withId(R.id.pager)).perform(clickXY(XCOORDINATE,YCOORDINATE)); //there's a better solution probably
      onView(withId(R.id.action_btn)).perform(click());
      if(popUp()){ //PUT SLEEPS
        onView(withId(R.id.message)).perform(pressBack());
      }
      if(!isLoggedIn()) {
        onView(withId(R.id.not_logged_in_close)).perform(click());
      }
      onView(withText(R.string.installing_msg)).check(matches(isDisplayed())); //Check download bar instead

      //click search button and search for an app

    }
  }

  ////////////////END TESTS/////////////////

  private boolean isFirstTime(){
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch(NoMatchingViewException e){
      return false;
    }
  }

  private boolean isLoggedIn(){
    try { onView(withId(R.id.show_login_with_aptoide_area)).check(matches(isDisplayed()));
      return false;
    } catch (NoMatchingViewException e) { return true; }
  }

  private boolean popUp(){
    try { onView(withId(R.id.message)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) { return false; }
  }

  private void goToMyAccount() throws InterruptedException {
    Thread.sleep(1000);
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    Thread.sleep(2000);
    onView(withText(R.string.my_account_title_my_account)).perform(click());//  TO DO Should work with navigation_item_my_account
    Thread.sleep(500);
  }


  private void performLogin() throws InterruptedException {
    Thread.sleep(500);
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.username)).perform(replaceText(EMAILTEST2));
    onView(withId(R.id.password)).perform(replaceText(PASS),closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
    //TO DO AFTER FLOW
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

  public static ViewAction clickXY(final int x, final int y){
    return new GeneralClickAction(
        Tap.SINGLE,
        new CoordinatesProvider() {
          @Override
          public float[] calculateCoordinates(View view) {

            final int[] screenPos = new int[2];
            view.getLocationOnScreen(screenPos);

            final float screenX = screenPos[0] + x;
            final float screenY = screenPos[1] + y;
            float[] coordinates = {screenX, screenY};

            return coordinates;
          }
        },
        Press.FINGER);
  }
}
