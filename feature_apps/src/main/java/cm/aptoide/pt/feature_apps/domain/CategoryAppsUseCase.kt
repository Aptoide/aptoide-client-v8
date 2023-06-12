package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject

class CategoryAppsUseCase @Inject constructor(
  private val appsRepository: AppsRepository
) : AppsListUseCase {

  /**
   * [source] - a categoryName to get apps for
   */
  override suspend fun getAppsList(source: String) =
    appsRepository.getCategoryAppsList(categoryName = source)
}
