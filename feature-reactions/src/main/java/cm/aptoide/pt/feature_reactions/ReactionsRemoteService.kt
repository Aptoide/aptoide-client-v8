package cm.aptoide.pt.feature_reactions

import cm.aptoide.pt.feature_reactions.data.network.DummyJSON
import cm.aptoide.pt.feature_reactions.data.network.ReactionsJson

interface ReactionsRemoteService {
  suspend fun getReactions(id: String?): ReactionsJson
  suspend fun deleteReaction(id: String): DummyJSON
  suspend fun setReaction(id: String): DummyJSON
}
