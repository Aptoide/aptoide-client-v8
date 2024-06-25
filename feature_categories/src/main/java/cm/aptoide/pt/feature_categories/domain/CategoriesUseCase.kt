package cm.aptoide.pt.feature_categories.domain

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import javax.inject.Inject

class CategoriesUseCase @Inject constructor(
  private val urlsCache: UrlsCache,
  private val categoriesRepository: CategoriesRepository,
  @StoreName private val storeName: String,
) {
  suspend fun getCategories(tag: String): List<Category> {
    val url = urlsCache.get(id = tag) ?: "store/groups/get/store_name=$storeName/groups_depth=1/group_name=games/hide_empty=1/hide_not_foreign=1/sort=score"
    return categoriesRepository.getCategoriesList(url)
  }
}
