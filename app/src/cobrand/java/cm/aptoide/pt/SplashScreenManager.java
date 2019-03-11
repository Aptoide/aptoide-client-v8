package cm.aptoide.pt;

import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.splashscreen.SplashScreenFragment;

public class SplashScreenManager {

  private FragmentNavigator fragmentNavigator;

  public SplashScreenManager(FragmentNavigator fragmentNavigator){
    this.fragmentNavigator = fragmentNavigator;
  }

  public void showSplashScreen() {
    fragmentNavigator.navigateTo(SplashScreenFragment.newInstance(), true);
  }
}
