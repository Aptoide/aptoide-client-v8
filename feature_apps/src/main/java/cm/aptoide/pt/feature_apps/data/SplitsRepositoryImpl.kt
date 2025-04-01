package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class SplitsRepositoryImpl @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val scope: CoroutineScope,
) : SplitsRepository {

  override suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplitJSON> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getDynamicSplits(md5 = md5).list
        ?: throw IllegalStateException()
    }

  internal interface Retrofit {
    @GET("app/getDynamicSplits")
    suspend fun getDynamicSplits(
      @Query("apk_md5sum") md5: String,
    ): BaseV7ListResponse<DynamicSplitJSON>
  }
}
