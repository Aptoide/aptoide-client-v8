package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_home.data.model.WidgetsJSON
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.feature_home.domain.Widget
import cm.aptoide.pt.feature_home.domain.WidgetAction
import cm.aptoide.pt.feature_home.domain.WidgetActionType
import cm.aptoide.pt.feature_home.domain.WidgetLayout
import cm.aptoide.pt.feature_home.domain.WidgetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url
import javax.inject.Inject

internal class AptoideWidgetsRepository @Inject constructor(
  private val widgetsRemoteDataSource: Retrofit,
  private val storeName: String,
  @WidgetsUrl private val widgetsUrl: String,
  private val scope: CoroutineScope,
) : WidgetsRepository {

  private val cachedGetStoreWidgets: MutableMap<String, String?> = mutableMapOf()

  override suspend fun getStoreWidgets(bypassCache: Boolean): List<Widget> =
    withContext(scope.coroutineContext) {
      widgetsRemoteDataSource.getStoreWidgets(
        url = widgetsUrl,
        storeName = storeName,
        bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
      )
        .datalist?.list?.mapNotNull { widget ->
          widget.toDomainModel()?.also {
            cachedGetStoreWidgets[it.tag] = it.view
            it.action?.forEach { action ->
              cachedGetStoreWidgets[action.tag] = action.url
            }
          }
        }
        ?: throw IllegalStateException()
    }

  override suspend fun getActionUrl(tag: String): String? =
    cachedGetStoreWidgets[tag]

  private fun WidgetsJSON.WidgetNetwork.toDomainModel(): Widget? {
    val type = this.type ?: return null
    return Widget(
      title = this.title!!,
      type = WidgetType.valueOf(type.name),
      layout = extractLayout(),
      view = this.view,
      tag = this.tag,
      action = extractWidgetListOfActions(),
      icon = this.data?.icon,
      graphic = this.data?.graphic,
      background = this.data?.background
    )
  }

  private fun WidgetsJSON.WidgetNetwork.extractWidgetListOfActions(): List<WidgetAction> =
    this.actions?.mapNotNull { actionJSON ->
      val tag = actionJSON.tag ?: return@mapNotNull null
      val url = actionJSON.event?.action ?: return@mapNotNull null
      WidgetAction(
        type = mapWidgetActionType(actionType = actionJSON.type),
        label = actionJSON.label,
        tag = tag,
        url = url
      )
    } ?: listOf()

  private fun mapWidgetActionType(actionType: String?): WidgetActionType {
    return when (actionType) {
      "bottom" -> WidgetActionType.BOTTOM
      "button" -> WidgetActionType.BUTTON
      else -> WidgetActionType.UNDEFINED
    }
  }

  private fun WidgetsJSON.WidgetNetwork.extractLayout() = try {
    WidgetLayout.valueOf(this.data!!.layout!!.name)
  } catch (e: NullPointerException) {
    WidgetLayout.UNDEFINED
  }

  interface Retrofit {
    @GET
    suspend fun getStoreWidgets(
      @Url url: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>
  }
}
