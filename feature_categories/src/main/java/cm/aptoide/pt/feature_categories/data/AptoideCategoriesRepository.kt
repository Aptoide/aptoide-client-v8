package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_categories.data.network.model.CategoryJson
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

internal class AptoideCategoriesRepository @Inject constructor(
  private val categoriesRemoteDataSource: Retrofit,
  private val storeName: String
) : CategoriesRepository {

  override fun getCategoriesList(url: String): Flow<List<Category>> = flow<List<Category>> {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("store/groups/get/")[1]
    val response = categoriesRemoteDataSource.getCategoriesList(query, storeName)
      .datalist?.list?.map {
        it.toDomainModel()
      }
      ?: throw IllegalStateException()
    emit(response)
  }.flowOn(Dispatchers.IO)

  internal interface Retrofit {
    @GET("store/groups/get/{query}")
    suspend fun getCategoriesList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<CategoryJson>
  }
}

fun CategoryJson.toDomainModel() = Category(
  id = id,
  name = name,
  title = title,
  icon = icon,
  graphic = graphic,
  background = background
)
