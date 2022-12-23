package cm.aptoide.pt.feature_appview.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_appview.data.network.model.RelatedCardJson

interface RemoteAppViewRepository {
  suspend fun getRelatedContent(packageName: String): BaseV7DataListResponse<RelatedCardJson>
}