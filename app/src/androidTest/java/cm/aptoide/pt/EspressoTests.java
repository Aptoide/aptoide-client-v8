package cm.aptoide.pt;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.AmbiguousViewMatcherException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.view.MainActivity;
import java.io.IOException;
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
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)

public class EspressoTests {

  private final String SIGNUPEMAILTESTBGN = "jose.messejana+";
  private final String SIGNUPEMAILTESTEND = "@aptoide.com";
  private final String LOGINEMAIL = "jose.messejana@aptoide.com";
  private final String PASS = "aptoide1234";
  private final String APP_TO_SEARCH = "Hearthstone";
  private final String LIGHT_APP_TO_SEARCH = "Cut the rope";
  private final String MATURE_SEARCH = "kaoiproduct.roulletesexfree";
  private final String MATURE_APP = "Roullete Sex (Roleta do Sexo)";
  private final int WAIT_TIME = 550;
  private final int LONGER_WAIT_TIME = 2000;
  private final int NUMBER_OF_RETRIES = 3;
  @Rule public IntentsTestRule<MainActivity> mActivityRule =
      new IntentsTestRule<>(MainActivity.class);
 // @Rule public RetryTestRule retry = new RetryTestRule(NUMBER_OF_RETRIES);
  private String SIGNUPEMAILTEST = "";
  private String STORENAME = "a";

  private static ViewAction swipeRigthOnLeftMost() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT, Press.FINGER);
  }

  public static Matcher<View> withRecyclerViewSize(final int size) {
    return new TypeSafeMatcher<View>() {

      @Override public boolean matchesSafely(final View view) {
        final int actualListSize = ((RecyclerView) view).getAdapter()
            .getItemCount();
        return actualListSize >= size;
      }

      @Override public void describeTo(final Description description) {
        description.appendText("RecyclerView should have " + size + " items");
      }
    };
  }

  @Before public void setUp() throws IOException {
    SIGNUPEMAILTEST = SIGNUPEMAILTESTBGN + System.currentTimeMillis() + SIGNUPEMAILTESTEND;
    STORENAME = STORENAME + System.currentTimeMillis();
    if (isFirstTime()) {
      skipWizard();
    }
    logOutorGoBack();

  }

  @Test public void emptyEmailSignIn() throws InterruptedException {
    goToMyAccount();
    performLogin("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  @Test public void emptyPasswordSignIn() throws InterruptedException {
    goToMyAccount();
    performLogin(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void emptySignIn() throws InterruptedException {
    goToMyAccount();
    performLogin("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void wrongSignIn() throws InterruptedException {
    goToMyAccount();
    performLogin(LOGINEMAIL, "wrongpass");
    onView(withText(R.string.error_invalid_grant)).check(matches(isDisplayed()));
  }

  @Test public void invalidEmailSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp("randomemail", PASS);
    onView(withText(R.string.ws_error_IARG_106)).check(matches(isDisplayed()));
  }

  @Test public void invalidPasswordSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp("randomemail", "igjsi1");
    onView(withText(R.string.password_validation_text)).check(matches(isDisplayed()));
  }

  @Test public void emptyEmailSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp("", PASS);
    onView(withText(R.string.no_email_error_message)).check(matches(isDisplayed()));
  }

  @Test public void emptyPasswordSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp(LOGINEMAIL, "");
    onView(withText(R.string.no_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void emptySignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp("", "");
    onView(withText(R.string.no_email_and_pass_error_message)).check(matches(isDisplayed()));
  }

  @Test public void emailExistsSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp(LOGINEMAIL, PASS);
    onView(withText(R.string.ws_error_WOP_9)).check(matches(isDisplayed()));
  }

  // Third batch of tests

  @Test public void signUp() throws InterruptedException {
    goToMyAccount();
    performSignUp(SIGNUPEMAILTEST, PASS);
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    completeSignUp();
  }

  @Test public void signUpPrivate() throws InterruptedException {
    goToMyAccount();
    performSignUp(SIGNUPEMAILTEST, PASS);
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    completeSignUpPrivate();
  }

  @Test public void signUpMoreInfoPublic() throws InterruptedException {
    goToMyAccount();
    performSignUp(SIGNUPEMAILTEST, PASS);
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    completeSignUpMoreInfoPublic();
  }

  @Test public void createStoreAtSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp(SIGNUPEMAILTEST, PASS);
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    completeSignUpWithStore();
    while(!hasLoggedIn()){
      Thread.sleep(WAIT_TIME);
    }
    goToMyAccount();
    onView(withId(R.id.my_account_store_name)).check(matches(withText(STORENAME)));
  }

  @Test public void createStoreAfterSignUp() throws InterruptedException {
    goToMyAccount();
    performSignUp(SIGNUPEMAILTEST, PASS);
    while (notSignUp()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    completeSignUp();
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.stores)).perform(click());
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.create_store_action)).perform(click());
    createStore();
    while(!hasLoggedIn()){
      Thread.sleep(WAIT_TIME);
    }
    onView(withText(R.string.stores)).perform(swipeRight());
    onView(withText(R.string.home_title)).perform(click());
    Thread.sleep(WAIT_TIME);
    goToMyAccount();
    onView(withId(R.id.my_account_store_name)).check(matches(withText(STORENAME)));
  }

/*  @Test public void profilePhoto() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    while (!hasLoggedIn()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_profile)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_user_image_action)).perform(click());
    Thread.sleep(WAIT_TIME);
    createImageandIntend();
    onView(withId(R.id.button_camera)).perform(click());
    allow_permission(mDevice, "CAMERA");
    allow_permission(mDevice, "WRITE_EXTERNAL_STORAGE");
    intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    Thread.sleep(LONGER_WAIT_TIME * 2);
    onView(withId(R.id.create_user_create_profile)).perform(click());
  }

  @Test public void storePhoto() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    while (!hasLoggedIn()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    goToMyAccount();
    onView(withId(R.id.my_account_edit_user_store)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_image_action)).perform(click());
    Thread.sleep(WAIT_TIME);
    createImageandIntend();
    onView(withId(R.id.button_camera)).perform(click());
    allow_permission(mDevice, "CAMERA");
    allow_permission(mDevice, "WRITE_EXTERNAL_STORAGE");
    intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    Thread.sleep(LONGER_WAIT_TIME * 2);
    onView(withId(R.id.create_store_action)).perform(click());
  } */

  @Test public void matureTest() throws InterruptedException {
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    try {
      onView(withText(R.string.yes)).perform(click());
    } catch (NoMatchingViewException e) {
      Thread.sleep(LONGER_WAIT_TIME);
      onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
      onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
      onView(withText(R.string.yes)).perform(click());
    }
    pressBackButton();
    Thread.sleep(WAIT_TIME);
    barOnlySearchApp(MATURE_SEARCH);
    onView(withId(R.id.search_src_text)).perform(pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    try{
    onView(withText(MATURE_APP)).check(matches(isDisplayed()));
    } catch(AmbiguousViewMatcherException e){ }
    pressBackButton();
    goToSettings();
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    pressBackButton();
    Thread.sleep(WAIT_TIME);
    barOnlySearchApp(MATURE_SEARCH);
    onView(withId(R.id.search_src_text)).perform(pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    try {
      onView(withText(MATURE_APP)).check(matches(not(isDisplayed())));
    }catch(NoMatchingViewException e1){ }
  }

 /* @Test public void landscapewizard() throws InterruptedException {
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.next_icon)).perform(swipeLeft());
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.skip_text)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.stores)).perform(click());
  } */

  @Test public void landscapehometab() throws InterruptedException {
    Activity activity = mActivityRule.getActivity();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.stores)).perform(click());
    onView(withText(R.string.home_title)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.recycler_view)).perform(
        RecyclerViewActions.actionOnItemAtPosition(1, click()));
  }

  @Test public void landscapeSearch() throws InterruptedException {
    Activity activity = mActivityRule.getActivity();
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(WAIT_TIME);
    barOnlySearchApp(APP_TO_SEARCH);
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).check(matches(isDisplayed()));
    onView(withId(R.id.all_stores_result_list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(0, click()));
  }

  @Test public void landscapeAppView() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    Activity activity = mActivityRule.getActivity();
    goToApp(APP_TO_SEARCH, 0);
    cancelIfDownloading();
    Thread.sleep(WAIT_TIME);
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_share)).check(matches(isDisplayed()));
    onView(withId(R.id.app_icon)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).perform(click());
    Thread.sleep(LONGER_WAIT_TIME);
    closePopUp();
    allow_permission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_pause)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_resume)).perform(click());
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.ic_action_cancel)).perform(click());
    onView(withId(R.id.menu_share)).check(matches(isDisplayed()));
    onView(withId(R.id.app_icon)).check(matches(isDisplayed()));
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
  }

  // Initial tests

  @Test public void settingsLandscape() throws InterruptedException {
    boolean checked;
    Activity activity = mActivityRule.getActivity();
    goToSettings();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    try {
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } catch (Exception e) {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    if (!checked) {
      onView(withText(R.string.yes)).perform(click());
      checked = true;
    } else {
      checked = false;
    }
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(11));
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(11, click()));
    Thread.sleep(WAIT_TIME);
    if (!checked) {
      onView(withText(R.string.yes)).perform(click());
    }
    Thread.sleep(LONGER_WAIT_TIME);
  }

  @Test public void signIn() throws InterruptedException {
    goToMyAccount();
    performLogin(LOGINEMAIL, PASS);
    while (!hasLoggedIn()) {
      Thread.sleep(LONGER_WAIT_TIME);
    }
    checkIfLoggedIn();
    goToMyAccount();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_logout)).check(matches(isDisplayed()));
  }

  @Test public void download() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToApp(APP_TO_SEARCH, 0);
    cancelIfDownloading();
    while (!hasOpenedAppView()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(LONGER_WAIT_TIME);
    closePopUp();
    allow_permission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.download_progress)).check(matches(isDisplayed()));
    testIcActionButtons();
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(isDisplayed()));
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.appview_button_install)));
  }

  /* @Test public void download_and_install() throws InterruptedException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    goToApp(LIGHT_APP_TO_SEARCH, 1);
    cancelIfDownloading();
    while (!hasOpenedAppView()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(LONGER_WAIT_TIME);
    closePopUp();
    allow_permission(mDevice, "WRITE_EXTERNAL_STORAGE");
    closeIfIsNotLoggedInOnDownload();
    Thread.sleep(LONGER_WAIT_TIME * 15);
    installApp(mDevice);
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.action_btn)).check(matches(withText(R.string.open)));
  } */

  private boolean isFirstTime() {
    try {
      onView(withId(R.id.next_icon)).check(matches(isDisplayed()));
      return true;
    } catch (NoMatchingViewException e) {
      return false;
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

  private boolean hasCreatedUser(boolean isMoreInfo) {
    try {
      if (isMoreInfo) {
        onView(withId(R.id.logged_in_more_info_button)).perform(click());
      } else {
        onView(withId(R.id.logged_in_continue)).perform(click());
      }
      return true;
    } catch (NoMatchingViewException e) {
      return false;
    }
  }

  private boolean hasCreatedStore() {
    try {
      onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
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
    } catch (Exception e) {
      return false;
    }
  }

  // IfElse Actions

  private boolean hasPermission(String permission) {
    Context context = InstrumentationRegistry.getTargetContext();
    String finalpermission = "android.permission." + permission;
    int permissionStatus = ContextCompat.checkSelfPermission(context, finalpermission);
    return (permissionStatus == PackageManager.PERMISSION_GRANTED);
  }

  private void closePopUp() throws InterruptedException {
    try {
      onView(withText("OK")).perform(click());
    } catch (NoMatchingViewException e) {
    }
  }

  private void allow_permission(UiDevice mDevice, String permission) {
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

  private void logOutorGoBack() {
    try {
      onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
      Thread.sleep(WAIT_TIME);
      onView(withId(R.id.profile_email_text)).perform(click());
      onView(withId(R.id.toolbar)).perform(swipeLeft());
      goToMyAccount();
      onView(withId(R.id.button_logout)).perform(click());
    } catch (Exception e) {
      onView(withId(R.id.toolbar)).perform(swipeLeft());
    }
  }

  //Refactored Methods

  private void cancelIfDownloading() {
    try {
      onView(withId(R.id.ic_action_cancel)).perform(click());
    } catch (Exception e) {
    }
  }

  private void goToMyAccount() throws InterruptedException {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.drawer_title_my_account)).perform(click());
  }

  private void performLogin(String email, String pass) throws InterruptedException {
    onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.button_login)).perform(click());
  }

  private void performSignUp(String email, String pass) throws InterruptedException {
    onView(withId(R.id.show_join_aptoide_area)).perform(click());
    onView(withId(R.id.username)).perform(replaceText(email));
    onView(withId(R.id.password)).perform(replaceText(pass), closeSoftKeyboard());
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

  private void goToApp(String app, int pos) throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(replaceText(app), pressImeActionButton());
    Thread.sleep(LONGER_WAIT_TIME);
    onView(withId(R.id.all_stores_result_list)).perform(
        RecyclerViewActions.actionOnItemAtPosition(pos, click()));
  }

  private void testIcActionButtons() throws InterruptedException {
    onView(withId(R.id.ic_action_pause)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.ic_action_resume)).perform(click());
    onView(withId(R.id.ic_action_cancel)).perform(click());
  }

  private void completeSignUp() throws InterruptedException {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    while (!hasCreatedUser(false)) {
      Thread.sleep(WAIT_TIME);
    }
    while (!hasCreatedStore()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpPrivate() throws InterruptedException {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    while (!hasCreatedUser(true)) {
      Thread.sleep(WAIT_TIME);
    }
    onView(withId(R.id.logged_in_private_button)).perform(click());
    while (!hasCreatedStore()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpMoreInfoPublic() throws InterruptedException {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    while (!hasCreatedUser(true)) {
      Thread.sleep(WAIT_TIME);
    }
    onView(withId(R.id.logged_in_continue)).perform(click());
    while (!hasCreatedStore()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_skip)).perform(click());
  }

  private void completeSignUpWithStore() throws InterruptedException {
    onView(withId(R.id.create_user_username_inserted)).perform(replaceText("a1"));
    onView(withId(R.id.create_user_create_profile)).perform(click());
    while (!hasCreatedUser(false)) {
      Thread.sleep(WAIT_TIME);
    }
    while (!hasCreatedStore()) {
      Thread.sleep(WAIT_TIME);
    }
    Thread.sleep(WAIT_TIME);
    createStore();
  }

  private void createStore() throws InterruptedException {
    onView(withId(R.id.create_store_name)).perform(replaceText(STORENAME), closeSoftKeyboard());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.create_store_choose_name_title)).perform(swipeUp());
    onView(withId(R.id.theme_selector)).perform(swipeUp());
    onView(withId(R.id.create_store_action)).perform(click());
  }

  private void barOnlySearchApp(String app) throws InterruptedException {
    onView(withId(R.id.action_search)).perform(click());
    Thread.sleep(WAIT_TIME);
    onView(withId(R.id.search_src_text)).perform(replaceText(app));
  }

  private void installApp(UiDevice mDevice) {
    try {
      mDevice.findObject(new UiSelector().clickable(true)
          .checkable(false)
          .textContains("INSTALL"))
          .click();
      Thread.sleep(LONGER_WAIT_TIME);
      mDevice.findObject(new UiSelector().clickable(true)
          .checkable(false)
          .textContains("DONE"))
          .click();
    } catch (Exception e1) {
    }
  }

  private void createImageandIntend() {
    Bitmap icon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext()
        .getResources(), R.mipmap.ic_launcher);

    // Build a result to return from the Camera app
    Intent resultData = new Intent();
    resultData.putExtra("data", icon);
    Instrumentation.ActivityResult result =
        new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
  }

  private void goToSettings() throws InterruptedException {
    onView(withId(R.id.toolbar)).perform(swipeRigthOnLeftMost());
    Thread.sleep(WAIT_TIME);
    onView(withText(R.string.drawer_title_settings)).perform(click());
  }

  private void pressBackButton() {
    ViewInteraction appCompatImageButton2 = onView(
        allOf(withContentDescription("Navigate up"), withParent(withId(R.id.toolbar)),
            isDisplayed()));
    appCompatImageButton2.perform(click());
  }
}
