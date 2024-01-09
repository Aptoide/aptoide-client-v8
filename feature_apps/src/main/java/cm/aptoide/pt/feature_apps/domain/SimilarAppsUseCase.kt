package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SimilarAppsUseCase @Inject constructor(
  private val appsRepository: AppsRepository
) : AppsListUseCase {

  /**
   * [source] - a packageName to use to search for a similar apps
   */
  override suspend fun getAppsList(source: String): List<App> =
    appsRepository.getRecommended(path = "package_name=$source")
}
