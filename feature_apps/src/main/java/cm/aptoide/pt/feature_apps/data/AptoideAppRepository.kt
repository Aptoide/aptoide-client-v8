package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.model.GetAppResponse
import cm.aptoide.pt.feature_apps.data.model.GetMetaResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

internal class AptoideAppRepository @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val storeName: String,
  private val mapper: AppMapper,
  private val scope: CoroutineScope,
) : AppRepository {

  override suspend fun getApp(packageName: String): App =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getApp(
        path = packageName,
        storeName = if (packageName != "com.appcoins.wallet") storeName else null,
      )
        .nodes.meta.data.let(mapper::map)
    }

  override suspend fun getAppMeta(source: String): App =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getAppMeta(path = source).data.let(mapper::map)
    }

  internal interface Retrofit {
    @GET("app/get/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String? = null,
    ): GetAppResponse

    @GET("app/getMeta/{source}")
    suspend fun getAppMeta(
      @Path(value = "source", encoded = true) path: String,
      @Query("store_name") storeName: String? = null,
    ): GetMetaResponse
  }
}
