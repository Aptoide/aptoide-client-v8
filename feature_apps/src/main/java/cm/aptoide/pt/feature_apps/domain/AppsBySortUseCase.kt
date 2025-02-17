package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.AppsListRepository
import javax.inject.Inject

class AppsBySortUseCase @Inject constructor(
  private val appsListRepository: AppsListRepository
) : AppsListUseCase {

  /**
   * [source] - a categoryName to get apps for
   */
  override suspend fun getAppsList(source: String) =
    appsListRepository.getSortedAppsList(
      sort = source,
      limit = 50
    )
}
