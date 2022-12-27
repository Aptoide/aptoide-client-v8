package cm.aptoide.pt.feature_editorial.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson

interface EditorialRemoteService {
  suspend fun getLatestEditorial(): BaseV7DataListResponse<EditorialJson>
  suspend fun getArticlesMeta(
    widgetUrl: String,
    subtype: String?
  ): BaseV7DataListResponse<EditorialJson>
  suspend fun getEditorialDetail(articleId: String): EditorialDetailJson
  suspend fun getRelatedContent(packageName: String): BaseV7DataListResponse<EditorialJson>
}