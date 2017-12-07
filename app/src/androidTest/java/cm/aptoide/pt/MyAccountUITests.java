package cm.aptoide.pt;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.skipWizard;

/**
 * Created by jose_messejana on 30-11-2017.
 */
@RunWith(AndroidJUnit4.class) public class MyAccountUITests {
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() {
    TestType.types = TestType.TestTypes.SIGNSIGNUPTESTS;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  @Test public void profilePhotoSuccess(){
    TestType.types = TestType.TestTypes.PHOTOSUCCESS;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    onView(withId(R.id.create_user_image_action)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withId(R.id.create_user_username_inserted)).perform(swipeUp());
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void profilePhotoLimitExceed(){
    TestType.types = TestType.TestTypes.PHOTOMIN;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    onView(withId(R.id.create_user_image_action)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withText("OK")).perform(click());
    TestType.types = TestType.TestTypes.PHOTOMAX;
    onView(withId(R.id.create_user_image_action)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withText("OK")).perform(click());
    onView(withId(R.id.create_user_username_inserted)).perform(swipeUp());
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void storePhotoSuccess(){
    TestType.types = TestType.TestTypes.PHOTOSUCCESS;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    onView(withId(R.id.create_store_image)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withId(R.id.create_store_image)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.create_store_action)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void storePhotoLimitExceed(){
    TestType.types = TestType.TestTypes.PHOTOMIN;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    onView(withId(R.id.create_store_image)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withText("OK")).perform(click());
    TestType.types = TestType.TestTypes.PHOTOMAX;
    onView(withId(R.id.create_store_image_action)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withText("OK")).perform(click());
    onView(withId(R.id.create_store_image)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.create_store_action)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

}
