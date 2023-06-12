package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject

class AppsByPackagesUseCase @Inject constructor(
  private val appsRepository: AppsRepository
) : AppsListUseCase {

  /**
   * [source] - a list of package names separated with a comma
   * Example:
   *      com.unicostudio.blastfriends,com.android.nonexistent,air.com.playtika.slotomania
   */
  override suspend fun getAppsList(source: String): List<App> =
    appsRepository.getAppsList(packageNames = source)
}
