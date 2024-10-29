package cm.aptoide.pt.installer.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.installer.DynamicSplit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class SplitsRepositoryImpl @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val scope: CoroutineScope,
) : SplitsRepository {

  override suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplit> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getDynamicSplits(md5 = md5).list
        ?.map(DynamicSplitJSON::toDomainModel)
        ?: throw IllegalStateException()
    }

  internal interface Retrofit {
    @GET("app/getDynamicSplits")
    suspend fun getDynamicSplits(
      @Query("apk_md5sum") md5: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7ListResponse<DynamicSplitJSON>
  }
}

fun DynamicSplitJSON.toDomainModel() = DynamicSplit(
  type = type,
  File(
    vername = "",
    vercode = 0,
    md5 = this.md5sum,
    filesize = this.filesize,
    path = this.path,
    path_alt = ""
  ),
  deliveryTypes = this.deliveryTypes,
  splits = this.splits.map { split ->
    File(
      vername = "",
      vercode = 0,
      md5 = split.md5sum,
      filesize = split.filesize,
      path = split.path,
      path_alt = ""
    )
  }
)
