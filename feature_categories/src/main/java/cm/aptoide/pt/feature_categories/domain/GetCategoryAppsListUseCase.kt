package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject

class GetCategoryAppsListUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  operator fun invoke(categoryName: String) =
    appsRepository.getCategoryAppsList(categoryName = categoryName)
}
