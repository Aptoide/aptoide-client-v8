package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject

class CategoryAppsUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  fun getApps(categoryName: String) =
    appsRepository.getCategoryAppsList(categoryName = categoryName)
}
