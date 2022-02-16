package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class AptoideAppsRepository : AppsRepository {
  override fun getAppsList(url: String): Flow<AppsResult> = flow {

    emit(AppsResult.Success(fakeAppsList()))

  }

  private fun fakeAppsList(): List<App> {
    val apps: ArrayList<App> = ArrayList()

    for (i in 0..10)
      apps.add(App("name $i", "icon $i"))

    return apps
  }

}