package cm.aptoide.pt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cm.aptoide.pt.UITests.goToMyAccount;
import static cm.aptoide.pt.UITests.skipWizard;

@RunWith(AndroidJUnit4.class) public class SignInSignUpUITests {
  private final String LOGINEMAIL = "jose.messejana@aptoide.com";
  private final String PASS = "aptoide1234";
  @Rule public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() {
    TestType.types = TestType.TestTypes.SIGNSIGNUPTESTS;
    TestType.initialization = TestType.TestTypes.REGULAR;
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
    onView(withText(R.string.ws_error_invalid_grant)).check(matches(isDisplayed()));
  }

  //
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

  //
  @Test public void signUpEmailExists() {
    TestType.types = TestType.TestTypes.USEDEMAIL;
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS + 5);
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

  @Test public void signOut() {
    TestType.initialization = TestType.TestTypes.LOGGEDIN;
    goToMyAccount();
    onView(withId(R.id.button_logout)).perform(click());
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

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
    onView(withId(R.id.action_search)).check(matches(isDisplayed()));
  }

  private void performLogin(String email, String pass) {
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(click());
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp(String email, String pass) {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(click());
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    onView(withId(R.id.button_sign_up)).perform(click());
  }

  private void completeSignUp() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_name)).perform(replaceText("somethingdoenstmatter"));
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpPrivate() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_private_button)).perform(click());
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpMoreInfoPublic() {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    onView(withId(R.id.logged_in_more_info_button)).perform(click());
    onView(withId(R.id.logged_in_continue)).perform(click());
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
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

  private void createStore() {
    onView(withId(R.id.create_store_name)).perform(replaceText("a name"));
    onView(withId(R.id.create_store_skip)).perform(scrollTo());
    onView(withId(R.id.create_store_action)).perform(click());
  }
}