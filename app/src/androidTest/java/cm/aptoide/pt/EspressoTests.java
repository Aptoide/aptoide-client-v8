package cm.aptoide.pt;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import cm.aptoide.pt.account.view.LoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jose_messejana on 26-09-2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTests {

  @Rule public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);


  @Test public void isCompletlyDisplayed() throws InterruptedException {
    Thread.sleep(2000);
      //onView(withId(R.id.button_login)).check(matches(not(isCompletelyDisplayed())));
      //onView(withId(R.id.button_login)).check(matches(isCompletelyDisplayed()));
      //onView(withId(R.id.button_login)).perform(click());
      onView(withId(R.id.button_login)).check(matches(withText("Login")));
  }


}
