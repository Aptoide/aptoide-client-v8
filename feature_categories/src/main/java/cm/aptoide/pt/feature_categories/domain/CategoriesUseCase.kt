package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import javax.inject.Inject

class CategoriesUseCase @Inject constructor(
  private val urlsCache: UrlsCache,
  private val categoriesRepository: CategoriesRepository,
) {
  suspend fun getCategories(tag: String): List<Category> =
    urlsCache.get(id = tag)
      ?.let { categoriesRepository.getCategoriesList(it) }
      ?: throw IllegalStateException("No url found")
}
