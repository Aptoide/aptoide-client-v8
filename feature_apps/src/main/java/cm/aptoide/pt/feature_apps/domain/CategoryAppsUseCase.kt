package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject

class CategoryAppsUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getApps(categoryName: String) =
    appsRepository.getCategoryAppsList(categoryName = categoryName)
}
