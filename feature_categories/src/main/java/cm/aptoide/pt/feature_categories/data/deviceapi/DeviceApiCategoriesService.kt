package cm.aptoide.pt.feature_categories.data.deviceapi

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** The categories of a catalog, apps and games alike, in one unpaginated answer. */
interface DeviceApiCategoriesService {

  @GET("android/v1/categories")
  suspend fun getCategories(
    @Query("variant") variant: String,
  ): CategoriesResponse
}

@Keep
data class CategoriesResponse(
  @SerializedName("categories") val categories: List<CategoryResponse>? = null,
)

@Keep
data class CategoryResponse(
  @SerializedName("slug") val slug: String? = null,
  @SerializedName("title") val title: String? = null,
  /** `apps` or `games`. */
  @SerializedName("parent") val parent: String? = null,
  @SerializedName("app_count") val appCount: Int? = null,
)
