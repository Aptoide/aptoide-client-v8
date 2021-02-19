package cm.aptoide.pt;

import android.Manifest;
import android.os.SystemClock;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.TestType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.skipWizard;

/**
 * Created by jose_messejana on 30-11-2017.
 */
@RunWith(AndroidJUnit4.class) public class MyAccountUITests {

  /**
   * Sets up the activity in which each test opens
   */
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  /**
   * Grants permission to simulate accessing storage
   */
  @Rule public GrantPermissionRule grantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

  /**
   * <p>Sets up which mocks to "activate"</p>
   * Skips Wizards in case it's the first time opening aptoide
   */
  @Before public void setUp() {
    TestType.types = TestType.TestTypes.SIGNSIGNUPTESTS;
    TestType.initialization = TestType.TestTypes.LOGGEDIN;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  /**
   * <p>User LoggedIn. Mock navigation to gallery</p>
   * Navigate to My Account. Presses Edit Profile. Presses user profile image. uploads a random
   * image. Navigate to myAccount
   */
  @Test public void profilePhotoSuccess() {
    TestType.types = TestType.TestTypes.PHOTOSUCCESS;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    onView(withId(R.id.create_user_image_action)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  /**
   * <p>User LoggedIn.  Mock navigation to gallery </p>
   * Navigate to My Account.Presses Edit Profile. Presses user profile image. uploads 5 images. 5
   * erros show. Navigate to myAccount
   */
  @Test public void profilePhotoErrors() {
    TestType.types = TestType.TestTypes.ERRORDECONDINGTEST;
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    displayErrors(true);
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  /**
   * <p>User LoggedIn.  Mock navigation to gallery </p>
   * Navigate to My Account.Presses Edit Store. Presses user profile image. uploads image. Navigate
   * to myAccount
   */
  @Test public void storePhotoSuccess() {
    TestType.types = TestType.TestTypes.PHOTOSUCCESS;
    TestType.initialization = TestType.TestTypes.LOGGEDINWITHSTORE;
    goToMyAccount();
    SystemClock.sleep(5000);
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    onView(withId(R.id.create_store_image)).perform(click());
    onView(withText("Select from gallery")).perform(click());
    onView(withId(R.id.create_store_action)).perform(scrollTo());
    onView(withId(R.id.create_store_action)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  /**
   * <p>User LoggedIn. Mock navigation to gallery </p>
   * Navigate to My Account.Presses Edit Profile. Presses user profile image. uploads 5 images. 5
   * erros show. Navigate to myAccount
   */
  @Test public void storePhotoErrors() {
    TestType.types = TestType.TestTypes.ERRORDECONDINGTEST;
    TestType.initialization = TestType.TestTypes.LOGGEDINWITHSTORE;
    goToMyAccount();
    SystemClock.sleep(5000);
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    displayErrors(false);
    onView(withId(R.id.create_store_action)).perform(scrollTo());
    onView(withId(R.id.create_store_action)).perform(click());
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  /**
   * <p>Clicks on image to upload photo</p>
   *
   * @param isUser True if it's the edit user view. False if it's the edit store view
   */
  private void uploadImageWithError(boolean isUser) {
    if (isUser) {
      onView(withId(R.id.create_user_image_action)).perform(click());
    } else {
      onView(withId(R.id.create_store_image)).perform(click());
    }
    onView(withText("Select from gallery")).perform(click());
    onView(withText("OK")).perform(click());
  }

  /**
   * Sets up to display the correct errors
   *
   * @param isUser True if it's the edit user view. False if it's the edit store view
   */
  private void displayErrors(boolean isUser) {
    uploadImageWithError(isUser);
    TestType.types = TestType.TestTypes.MIN_HEIGHTTEST;
    uploadImageWithError(isUser);
    TestType.types = TestType.TestTypes.MIN_WIDTHTEST;
    uploadImageWithError(isUser);
    TestType.types = TestType.TestTypes.MAX_HEIGHTTEST;
    uploadImageWithError(isUser);
    TestType.types = TestType.TestTypes.MAX_WIDTHTEST;
    uploadImageWithError(isUser);
    TestType.types = TestType.TestTypes.MAX_IMAGE_SIZETEST;
    uploadImageWithError(isUser);
  }
}
