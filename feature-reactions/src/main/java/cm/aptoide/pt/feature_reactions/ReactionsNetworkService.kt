package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class ReactionsNetworkService(private val reactionsRemoteDataSource: Retrofit) :
  ReactionsRemoteService {
  override suspend fun getReactions(id: String?): Response<ReactionsJson> {
    return reactionsRemoteDataSource.getTopReactionsResponse("CURATION_1", id)
  }

  interface Retrofit {
    @GET("groups/{group_id}/objects/{id}/reactions/summary")
    suspend fun getTopReactionsResponse(
      @Path("group_id") groupId: String?,
      @Path("id") id: String?,
    ): Response<ReactionsJson>
  }
}