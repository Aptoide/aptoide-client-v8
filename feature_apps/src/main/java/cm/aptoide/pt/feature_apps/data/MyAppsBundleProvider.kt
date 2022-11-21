package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface MyAppsBundleProvider {

  fun getBundleApps(): Flow<List<MyAppsApp>>
}
