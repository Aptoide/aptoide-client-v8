package cm.aptoide.pt.feature_editorial.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import retrofit2.Response

interface EditorialRemoteService {
  suspend fun getLatestEditorial(): Response<BaseV7DataListResponse<EditorialJson>>
  suspend fun getArticleMeta(widgetUrl: String): Response<BaseV7DataListResponse<EditorialJson>>
  suspend fun getEditorialDetail(articleId: String): Response<EditorialDetailJson>
}