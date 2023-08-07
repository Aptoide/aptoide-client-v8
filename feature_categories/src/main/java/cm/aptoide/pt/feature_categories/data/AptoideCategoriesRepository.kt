package cm.aptoide.pt.feature_categories.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import cm.aptoide.pt.feature_categories.data.model.AppCategoryJSON
import cm.aptoide.pt.feature_categories.data.model.CategoryJson
import cm.aptoide.pt.feature_categories.data.model.Names
import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category
import kotlinx.coroutines.flow.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

internal class AptoideCategoriesRepository @Inject constructor(
  private val categoriesRemoteDataSource: Retrofit,
  private val storeName: String,
  private val analyticsInfoProvider: AptoideAnalyticsInfoProvider,
  private val messagingInfoProvider: AptoideFirebaseInfoProvider,
) : CategoriesRepository {

  override suspend fun getCategoriesList(url: String): List<Category> {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("store/groups/get/")[1]
    return categoriesRemoteDataSource.getCategoriesList(query, storeName)
      .datalist?.list?.map {
        it.toDomainModel()
      }
      ?: throw IllegalStateException()
  }

  override suspend fun getGlobalCategoriesList(url: String): List<Category> {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("store/groups/get/")[1]
    return categoriesRemoteDataSource.getGlobalCategoriesList(query)
      .datalist?.list?.map {
        it.toDomainModel()
      }
      ?: throw IllegalStateException()
  }

  override suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory> {
    val chunkSize = 100
    val chunkedLists = packageNames.chunked(chunkSize)
    val appsList = ArrayList<AppCategory>()
    val analyticsId = analyticsInfoProvider.getAnalyticsId()
    val firebaseToken = messagingInfoProvider.getFirebaseToken()

    for (chunkedList in chunkedLists) {
      try {
        val result =
          categoriesRemoteDataSource.getAppsCategories(
            names = Names(chunkedList),
            storeName = storeName,
            analyticsId = analyticsId,
            firebaseToken = firebaseToken
          )
            .list
            ?.map { AppCategory(it.name, it.type) }
            ?: emptyList()
        appsList.addAll(result)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    return appsList
  }

  internal interface Retrofit {
    @GET("store/groups/get/{query}")
    suspend fun getCategoriesList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<CategoryJson>

    @GET("apks/groups/get/{query}")
    suspend fun getGlobalCategoriesList(
      @Path(value = "query", encoded = true) path: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<CategoryJson>

    @POST("hub/apps/get/")
    suspend fun getAppsCategories(
      @Query("user_uid") analyticsId: String?,
      @Query("store_name") storeName: String,
      @Query("firebase_token") firebaseToken: String?,
      @Body names: Names,
    ): BaseV7ListResponse<AppCategoryJSON>
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
