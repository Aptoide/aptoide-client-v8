package cm.aptoide.pt.apps

import cm.aptoide.pt.feature_apps.data.MyAppsApp
import cm.aptoide.pt.feature_apps.data.MyAppsBundleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AptoideMyAppsBundleProvider : MyAppsBundleProvider {

  override fun getBundleApps(): Flow<List<MyAppsApp>> {
    return flowOf(emptyList())
  }
}
