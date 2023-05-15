package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import javax.inject.Inject

class CategoriesUseCase @Inject constructor(
  private val categoriesRepository: CategoriesRepository,
) {
  suspend fun getCategories(tag: String): List<Category> =
    categoriesRepository.getHomeBundleActionListCategories(tag)
}
