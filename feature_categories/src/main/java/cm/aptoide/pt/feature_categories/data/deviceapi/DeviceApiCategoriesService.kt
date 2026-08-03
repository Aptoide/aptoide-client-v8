package cm.aptoide.pt.feature_categories.data.deviceapi

import androidx.annotation.Keep
import retrofit2.http.GET

/** `GET /android/v1/categories` — browseable categories (device.openapi.json). */
interface DeviceApiCategoriesService {

  @GET("android/v1/categories")
  suspend fun getCategories(): CategoriesResponse
}

@Keep
data class CategoriesResponse(val categories: List<CategoryResponse>? = null)

@Keep
data class CategoryResponse(
  val slug: String? = null,
  val title: String? = null,
  val parent: String? = null,
  val appCount: Int? = null,
)
