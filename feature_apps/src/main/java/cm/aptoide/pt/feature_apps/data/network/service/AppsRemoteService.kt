package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.network.model.AppCategoryJSON
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.GetAppResponse
import cm.aptoide.pt.feature_apps.data.network.model.GroupJSON

internal interface AppsRemoteService {

  suspend fun getAppsList(query: String): BaseV7DataListResponse<AppJSON>

  suspend fun getAppsList(groupId: Long): BaseV7DataListResponse<AppJSON>

  suspend fun getApp(packageName: String): GetAppResponse

  suspend fun getRecommended(url: String): BaseV7DataListResponse<AppJSON>

  suspend fun getAppVersionsList(packageName: String): BaseV7ListResponse<AppJSON>

  suspend fun getAppGroupsList(
    packageName: String,
    groupId: Long?
  ): BaseV7DataListResponse<GroupJSON>

  suspend fun getAppCategories(packageNames: List<String>): BaseV7ListResponse<AppCategoryJSON>
}
