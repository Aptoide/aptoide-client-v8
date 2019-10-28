package cm.aptoide.pt.home.apps.seemore

import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment

class SeeMoreAppcNavigator(private val appNavigator: AppNavigator) {

  fun navigateToAppView(appId: Long, packageName: String) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "")
  }

  fun navigateToAppViewAndInstall(appId: Long, packageName: String) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_AND_INSTALL,
        "")
  }
}