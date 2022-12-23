package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.DummyJSON
import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class ReactionsNetworkService(private val reactionsRemoteDataSource: Retrofit) :
  ReactionsRemoteService {
  override suspend fun getReactions(id: String?): ReactionsJson {
    return reactionsRemoteDataSource.getTopReactionsResponse("CURATION_1", id)
  }

  override suspend fun deleteReaction(id: String): DummyJSON {
    return reactionsRemoteDataSource.deleteReaction(id)
  }

  override suspend fun setReaction(id: String): DummyJSON {
    return reactionsRemoteDataSource.setReaction(Body("", "", ""))
  }

  interface Retrofit {
    @GET("groups/{group_id}/objects/{id}/reactions/summary")
    suspend fun getTopReactionsResponse(
      @Path("group_id") groupId: String?,
      @Path("id") id: String?,
    ): ReactionsJson

    @DELETE("reactions/{uid}/")
    suspend fun deleteReaction(
      @Path("uid") id: String?,
    ): DummyJSON

    @POST("reactions/{uid}")
    suspend fun setReaction(
      @retrofit2.http.Body body: Body,
    ): DummyJSON
  }
}

data class Body(
  private val objectUid: String,
  private val groupUid: String,
  private val type: String,
)

