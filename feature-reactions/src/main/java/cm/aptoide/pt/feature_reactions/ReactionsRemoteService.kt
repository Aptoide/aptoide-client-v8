package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.DummyJSON
import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import retrofit2.Response

interface ReactionsRemoteService {
  suspend fun getReactions(id: String?): Response<ReactionsJson>
  suspend fun deleteReaction(id: String): Response<DummyJSON>
  suspend fun setReaction(id: String): Response<DummyJSON>
}
