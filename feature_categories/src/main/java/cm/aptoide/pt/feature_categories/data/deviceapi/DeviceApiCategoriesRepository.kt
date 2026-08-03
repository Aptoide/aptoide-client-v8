package cm.aptoide.pt.feature_categories.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * Device-API categories (aptoideGamesDev only). `GET /categories` for the list;
 * the slug is the domain `name` used to browse (`/apps?category=`). `getAppsCategories`
 * (installed→category classification) has no device endpoint (leftover) → empty.
 */
class DeviceApiCategoriesRepository(
  private val service: DeviceApiCategoriesService,
  private val scope: CoroutineScope,
) : CategoriesRepository {

  override suspend fun getCategoriesList(url: String): List<Category> = fetchCategories()

  override suspend fun getGlobalCategoriesList(url: String): List<Category> = fetchCategories()

  override suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory> = emptyList()

  private suspend fun fetchCategories(): List<Category> = withContext(scope.coroutineContext) {
    deviceApiCall { service.getCategories() }.categories.orEmpty().mapNotNull { it.toCategory() }
  }

  private fun CategoryResponse.toCategory(): Category? {
    val name = slug ?: return null
    return Category(
      id = name.hashCode().toLong(),
      name = name,
      title = title ?: name,
      icon = null,
      graphic = null,
      background = null,
    )
  }
}
