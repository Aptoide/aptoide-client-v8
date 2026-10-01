package cm.aptoide.pt.feature_categories.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Reads the categories of the catalog [variant] from the device API. Failures of the service
 * reach the caller as a [cm.aptoide.pt.device_api.error.DeviceApiException], connectivity ones
 * as they are.
 */
class DeviceApiCategoriesDataSource(
  private val service: DeviceApiCategoriesService,
  private val variant: String,
  private val dispatcher: CoroutineDispatcher,
) {

  /**
   * The categories under [parent], one of [cm.aptoide.pt.device_api.catalog.CatalogParent], in
   * the order the service sent them. Each is named by its slug, which is what its apps are
   * browsed by. Those known to have no apps are left out.
   */
  suspend fun categories(parent: String): List<Category> = withContext(dispatcher) {
    deviceApiCall { service.getCategories(variant) }
      .categories.orEmpty()
      .filter { it.parent == parent && it.appCount != 0 }
      .mapNotNull { it.toCategory() }
  }

  private fun CategoryResponse.toCategory(): Category? {
    val slug = slug?.takeIf { it.isNotBlank() } ?: return null
    return Category(
      // The device API has no numeric ids and lists only need them to be distinct
      id = slug.hashCode().toLong(),
      name = slug,
      title = title?.takeIf { it.isNotBlank() } ?: slug,
    )
  }
}
