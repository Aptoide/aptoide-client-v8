package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson
import retrofit2.Response

interface ReactionsRemoteService {
  suspend fun getReactions(id: String?): Response<ReactionsJson>
}
