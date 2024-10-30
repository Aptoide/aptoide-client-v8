package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import javax.inject.Inject

class AppsByPackagesUseCase @Inject constructor(
  private val appsListRepository: AppsListRepository
) : AppsListUseCase {

  /**
   * [source] - a list of package names separated with a comma
   * Example:
   *      com.unicostudio.blastfriends,com.android.nonexistent,air.com.playtika.slotomania
   */
  override suspend fun getAppsList(source: String): List<App> =
    appsListRepository.getAppsList(packageNames = source)
}
