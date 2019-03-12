package cm.aptoide.pt.splashscreen;

import cm.aptoide.pt.home.BottomNavigationNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.splashscreen.SplashScreenFragment;

public class SplashScreenNavigator {

  private BottomNavigationNavigator bottomNavigationNavigator;
  private FragmentNavigator fragmentNavigator;

  public SplashScreenNavigator(BottomNavigationNavigator bottomNavigationNavigator,
      FragmentNavigator fragmentNavigator) {
    this.bottomNavigationNavigator = bottomNavigationNavigator;
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToSplashScreen() {
    fragmentNavigator.navigateTo(new SplashScreenFragment(), true);
  }

  public void navigateToHome() {
    bottomNavigationNavigator.navigateToHome();
  }
}
