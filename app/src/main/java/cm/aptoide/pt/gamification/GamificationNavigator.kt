package cm.aptoide.pt.gamification

import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment

class GamificationNavigator(private var appNavigator: AppNavigator) {

  fun navigateToAppViewAndInstall(packageName: String?) {
    appNavigator.navigateWithPackageName(packageName, AppViewFragment.OpenType.OPEN_AND_INSTALL)
  }

}