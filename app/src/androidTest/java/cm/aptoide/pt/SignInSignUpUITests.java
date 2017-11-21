package cm.aptoide.pt;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.content.ContextCompat;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.skipWizard;

/**
 * Created by jose_messejana on 24-10-2017.
 */

@RunWith(AndroidJUnit4.class)
public class SignInSignUpUITests {
  private final String LOGINEMAIL = "jose.messejana@aptoide.com";
  private final String PASS = "aptoide1234";
  private final String STORENAME = "a";
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);
  @Rule public RetryTestRule retry = new RetryTestRule(UITests.NUMBER_OF_RETRIES);
  private int nonerrormessagesphotos = 0;

  @Before public void setUp() {
    TestType.types = TestType.TestTypes.SIGNSIGNUPTESTS;
    if (UITests.isFirstTime()) {
     skipWizard();
    }
  }

  @Test public void signInEmptyEmail() {
    goToMyAccount();
    performLogin("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signInEmptyPasswordSignIn() {
    goToMyAccount();
    performLogin(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signInEmpty() {
    goToMyAccount();
    performLogin("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signInWrong() {
    TestType.types = TestType.TestTypes.SIGNINWRONG;
    goToMyAccount();
    performLogin(LOGINEMAIL, "wrongpass");
    onView(withText(R.string.error_invalid_grant)).check(matches(isDisplayed()));
  }

  @Test public void signUpInvalidEmail() {
    TestType.types = TestType.TestTypes.INVALIDEMAIL;
    goToMyAccount();
    performSignUp("randomemail", PASS);
    onView(withText(R.string.ws_error_IARG_106)).check(matches(isDisplayed()));
  }

  @Test public void signUpInvalidPassword() {
    goToMyAccount();
    performSignUp("randomemail", "igjsi1");
    onView(withText(R.string.password_validation_text)).check(matches(isDisplayed()));
  }

  @Test public void signUpEmptyEmail() {
    goToMyAccount();
    performSignUp("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signUpEmptyPassword() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signUpEmpty() {
    goToMyAccount();
    performSignUp("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void signUpEmailExists() {
    TestType.types = TestType.TestTypes.USEDEMAIL;
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS+5);
    onView(withText(R.string.ws_error_WOP_9)).check(matches(isDisplayed()));
  }

  @Test public void signUp() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUp();
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  @Test public void signUpPrivateUser() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpPrivate();
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  @Test public void signUpMoreInfoPublicUser() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpMoreInfoPublic();
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  @Test public void signUpWithCreateStore() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpWithStore();
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  @Test public void signIn() {
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  @Test public void signOut(){
    TestType.types = TestType.TestTypes.LOGGEDIN;
    goToMyAccount();
    onView(withId(R.id.button_logout)).perform(click());
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

 /* @Test public void profilePhoto() throws InterruptedException, UiObjectNotFoundException { //Arrastar as imagens primeiro
    nonerrormessagesphotos = 0;
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    int deviceHeight = mDevice.getDisplayHeight();
    int deviceWidth = mDevice.getDisplayWidth();
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    onView(withId(R.id.create_user_image_action)).perform(click());
    allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
    onView(withId(R.id.button_gallery)).perform(click());
    getPhotoFromStorage(mDevice, deviceHeight, deviceWidth, "user");
    if(nonerrormessagesphotos != 1){
      throw new IllegalStateException("Unexpected number of error messages displayed"); //if it has different than 1 means that one of the photos didn't display the error or the accepted photo displayed an error
    }
    onView(withId(R.id.create_user_create_profile)).perform(click());
  }

  @Test public void storePhoto() throws InterruptedException, UiObjectNotFoundException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    int deviceHeight = mDevice.getDisplayHeight();
    int deviceWidth = mDevice.getDisplayWidth();
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    onView(withId(R.id.create_store_image_action)).perform(click());
    allowPermission(mDevice, "WRITE_EXTERNAL_STORAGE");
    onView(withId(R.id.button_gallery)).perform(click());
    getPhotoFromStorage(mDevice, deviceHeight, deviceWidth, "store");
    if(nonerrormessagesphotos != 1){
      throw new IllegalStateException("Unexpected number of error messages displayed"); //if it has different than 1 means that one of the photos didn't display the error or the accepted photo displayed an error
    }
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.create_store_action)).perform(click());
  }
*/

  private void goToMyAccount() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withText(R.string.drawer_title_my_account)).perform(click());
  }

  private boolean hasPermission(String permission) {
    Context context = InstrumentationRegistry.getTargetContext();
    String finalpermission = "android.permission." + permission;
    int permissionStatus = ContextCompat.checkSelfPermission(context, finalpermission);
    return (permissionStatus == PackageManager.PERMISSION_GRANTED);
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

  private void performLogin(String email, String pass){
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp(String email, String pass) {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_sign_up)).perform(click());
  }

  private void completeSignUp() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpPrivate() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_private_button)).perform(click());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpMoreInfoPublic() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpWithStore() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    createStore();
  }

  private void createStore(){
    onView(withId(R.id.create_store_name)).perform(replaceText(STORENAME), closeSoftKeyboard());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.create_store_action)).perform(click());
  }

  //SECOND BATCH METHODS

  private void getPhotoFromStorage(UiDevice mDevice, int height, int width, String test)
      throws UiObjectNotFoundException, InterruptedException {
   /* try {
      mDevice.findObject(new UiSelector().clickable(true)
          .textContains("Gallery"))
          .click();
    } catch (Exception e1) {
    } */
    if (android.os.Build.VERSION.SDK_INT < 23) {
      mDevice.click(width / 8 * 7, height / 8);
      try {
        onView(withText("OK")).perform(click());
      } catch (Exception e) {
        nonerrormessagesphotos++;
      }
      goToImageStorage(test);
      mDevice.click(width / 2, height / 8);
      try {
        onView(withText("OK")).perform(click());
      } catch (Exception e) {
        nonerrormessagesphotos++;
      }
      goToImageStorage(test);
      mDevice.click(width / 8, height / 8);
      try {
        onView(withText("OK")).perform(click());
      } catch (Exception e) {
        nonerrormessagesphotos++;
      }
    } else if (android.os.Build.VERSION.SDK_INT == 23 || android.os.Build.VERSION.SDK_INT == 24) {
      for(int i = 0; i<3; i++) {
        mDevice.findObject(new UiSelector().clickable(true)
            .index(1))
            .click();
        mDevice.findObject(new UiSelector().clickable(true)
            .index(i))
            .click();
        try {
          onView(withText("OK")).perform(click());
        } catch (Exception e) {
          nonerrormessagesphotos++;
        }
        if(i<2) {
          goToImageStorage(test);
        }
      }
    } else {
      throw new IllegalArgumentException("This test (photo) doesn't work on Android 7");
    }
  }

  private void goToImageStorage(String test) throws InterruptedException {
    if(test.equals("user")){
      onView(withId(R.id.create_user_image_action)).perform(click());
    }
    else{
      onView(withId(R.id.create_store_image_action)).perform(click());
    }
    onView(withId(R.id.button_gallery)).perform(click());
  }

}
