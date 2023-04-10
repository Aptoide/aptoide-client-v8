package cm.aptoide.pt.feature_categories.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_categories.data.network.model.CategoryJson

internal interface CategoriesRemoteService {

  suspend fun getCategoriesList(query: String): BaseV7DataListResponse<CategoryJson>
}
