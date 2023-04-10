package cm.aptoide.pt.feature_categories.domain.usecase

import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesListUseCase @Inject constructor(
  private val categoriesRepository: CategoriesRepository
) {
  operator fun invoke(url: String): Flow<List<Category>> = categoriesRepository.getCategoriesList(url)
}
