package cm.aptoide.pt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.TestType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.skipWizard;

@RunWith(AndroidJUnit4.class) public class SignInSignUpUITests {
  private final String LOGINEMAIL = "jose.messejana@aptoide.com";
  private final String PASS = "aptoide1234";
  /**
   * Sets up the activity in which each test opens
   */
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  /**
   * <p>Sets up which mocks to "activate"</p>
   * Skips Wizards in case it's the first time opening aptoide
   */
  @Before public void setUp() {
    TestType.types = TestType.TestTypes.SIGNSIGNUPTESTS;
    TestType.initialization = TestType.TestTypes.REGULAR;
    if (UITests.isFirstTime()) {
      skipWizard();
    }
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses Login Button, inserts an empty
   * email and random password.</p>
   * Result: Appropriate error message should be displayed"
   */
  @Test public void signInEmptyEmail() {
    goToMyAccount();
    performLogin("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses Login Button, inserts random
   * valid
   * email and empty password.</p>
   * Result: Appropriate error message should display
   */
  @Test public void signInEmptyPassword() {
    goToMyAccount();
    performLogin(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses Login Button, inserts empty
   * email
   * and password.</p>
   * Result: Appropriate error message should display
   */
  @Test public void signInEmpty() {
    goToMyAccount();
    performLogin("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses Login Button, inserts a random
   * valid email and password. Mock error response </p>
   * Result: Appropriate error message should display
   */
  @Test public void signInWrong() {
    TestType.types = TestType.TestTypes.SIGNINWRONG;
    goToMyAccount();
    performLogin(LOGINEMAIL, "wrongpass");
    onView(withText(R.string.ws_error_invalid_grant)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts an
   * email
   * without "@" and a random password. Mock error response </p>
   * Result: Appropriate error message should display
   */
  @Test public void signUpInvalidEmail() {
    TestType.types = TestType.TestTypes.INVALIDEMAIL;
    goToMyAccount();
    performSignUp("randomemail", PASS);
    onView(withText(R.string.ws_error_IARG_106)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and a password with less than 8 characters. </p>
   * Result: Appropriate error message should display
   */
  @Test public void signUpInvalidPasswordLength() {
    goToMyAccount();
    performSignUp("randomemail", "igjsi1");
    onView(withText(R.string.password_validation_text)).check(matches(isDisplayed()));
  }

  /**
   * User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and a password with invalid characters combination.
   * Result: Appropriate error message should display
   */
  @Test public void signUpInvalidPasswordCharacters() {
    goToMyAccount();
    performSignUp("randomemail", "igjsi1");
    onView(withText(R.string.password_validation_text)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts empty
   * email
   * and random password </p>
   * Result: Appropriate error message should display
   */
  @Test public void signUpEmptyEmail() {
    goToMyAccount();
    performSignUp("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and empty password. </p>
   * Result: Appropriate error message should display
   */
  @Test public void signUpEmptyPassword() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts empty
   * email
   * and password.</p>
   * Result: Appropriate error message should display
   */
  @Test public void signUpEmpty() {
    goToMyAccount();
    performSignUp("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and valid password. Mock error response </p>
   * Result:Appropriate error message should display
   */
  @Test public void signUpEmailExists() {
    TestType.types = TestType.TestTypes.USEDEMAIL;
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS + 5);
    onView(withText(R.string.ws_error_WOP_9)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and random valid password.</p>
   * Result: Navigate to the necessary views to complete public registration and in the end
   * navigates to HomeFragment.
   */
  @Test public void signUp() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUp();
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and random valid password. </p>
   * Result: Navigate to the necessary views to complete private registration and in the end
   * navigates to HomeFragment.
   */
  @Test public void signUpPrivateUser() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpPrivate();
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and random valid password.</p>
   * Result: Navigate to the necessary views to complete public registration passing through the
   * MoreInfoView and in the end navigates to HomeFragment.
   */
  @Test public void signUpMoreInfoPublicUser() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpMoreInfoPublic();
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p>User navigates to the LoginSignUpCredentialFragment, presses SignUp Button, inserts random
   * valid email and random valid password.</p>
   * Result: Navigate to the necessary views to complete public registration, pressing create store
   * in the last step and in the end navigates to HomeFragment.
   */
  @Test public void signUpWithCreateStore() {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    completeSignUpWithStore();
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p> User navigates to the LoginSignUpCredentialFragment, presses Login Button, inserts random
   * valid
   * email and random valid password. </p>
   * Result: Navigate to HomeFragment
   */
  @Test public void signIn() {
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p>Mock user logged in</p>
   * <p>User navigates to MyAccount and presses Log Out</p>
   * Result: Navigate to HomeFragment
   */
  @Test public void signOut() {
    TestType.initialization = TestType.TestTypes.LOGGEDIN;
    goToMyAccount();
    onView(withId(R.id.button_logout)).perform(click());
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * <p>First time opening Aptoide, skips first 2 views, presses login in the third one, insert
   * random
   * valid username and password </p>
   * Result: Navigate to HomeFragment
   */
  @Test public void signInFromWizard() {
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    preferences.edit()
        .clear()
        .apply();
    activity1.finish();
    mActivityRule.launchActivity(new Intent());
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    performLogin(LOGINEMAIL, PASS);
  }

  /**
   * <p>First time opening Aptoide, skips first 2 views, presses signUp in the third one, insert
   * random
   * valid username and password </p>
   * Result: Navigate to the necessary views to complete public registration and in the end
   * navigates to HomeFragment.
   */
  @Test public void signUpFromWizard() {
    Activity activity1 = mActivityRule.getActivity();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity1);
    preferences.edit()
        .clear()
        .apply();
    activity1.finish();
    mActivityRule.launchActivity(new Intent());
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    performSignUp(LOGINEMAIL, PASS);
    completeSignUp();
    onView(withId(R.id.menu_item_search)).check(matches(isDisplayed()));
  }

  /**
   * Presses Login Button. Inserts email and Password
   */
  private void performLogin(String email, String pass) {
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.email)).perform(click());
    onView(withId(R.id.email)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(click());
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
  }

  /**
   * Presses Sign Up Button. Inserts email and Password
   */
  private void performSignUp(String email, String pass) {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.email)).perform(click());
    onView(withId(R.id.email)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(click());
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_sign_up)).perform(click());
  }

  /**
   * Goes trough all the minimum necessary views to complete registration
   */
  private void completeSignUp() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_name)).perform(replaceText("somethingdoenstmatter"));
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  /**
   * <p>Goes trough all the necessary views to complete private registration.</p>
   * On second view press More Info and on the next one press private
   */
  private void completeSignUpPrivate() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_private_button)).perform(click());
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  /**
   * <p>Goes trough all the necessary views to complete registration passing by the More Info
   * view.</p>
   * On second view press More Info and on the next one press continue
   */
  private void completeSignUpMoreInfoPublic() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  /**
   * <p>Goes through all the minimum necessary views to complete registration.</p>
   * On the last view (third), press create Store
   */
  private void completeSignUpWithStore() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    createStore();
  }

  /**
   * Choose a random name and click on create Store
   */
  private void createStore() {
    onView(withId(R.id.create_store_name)).perform(replaceText("a name"));
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_action)).perform(click());
  }
}