package cm.aptoide.pt;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTests {

  //private final String EMAILTEST = "jose.messejana@gmail.com";
  private final String EMAILTEST2 = "Jose.Messejana@aptoide.com";
  private final String PASS = "passwordteste0123";

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


  @Test public void signIn() throws InterruptedException {


      ///////////////// Login Activity ////////////////
      Thread.sleep(500);
      onView(withId(R.id.show_login_with_aptoide_area)).perform(click());
      Thread.sleep(1000);
      onView(withId(R.id.username)).perform(replaceText(EMAILTEST2));
      onView(withId(R.id.password)).perform(replaceText(PASS));
      onView(withId(R.id.button_login)).perform(click());

     // onView(withText("Please wait...")).check(matches(isDisplayed()));
     // onView(withId(R.id.button_login)).check(matches(withText("Login")));
  }


}
