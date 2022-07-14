package cm.aptoide.pt.feature_editorial.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import retrofit2.Response
import retrofit2.http.GET

class EditorialNetworkService(private val editorialRemoteDataSource: Retrofit) :
  EditorialRemoteService {

  override suspend fun getLatestEditorial(): Response<BaseV7DataListResponse<EditorialJson>> {
    return editorialRemoteDataSource.getLatestEditorial()
  }

  interface Retrofit {
    @GET("get/type=CURATION_1/aptoide_uid=0/limit=1")
    suspend fun getLatestEditorial(): Response<BaseV7DataListResponse<EditorialJson>>
  }
}
