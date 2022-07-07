package cm.aptoide.pt.editorial.epoxy

import cm.aptoide.pt.reactions.ReactionEvent
import cm.aptoide.pt.reactions.data.TopReaction
import rx.Observable

interface ReactionView {
  fun getReactionEvent(): Observable<ReactionEvent>
  fun reactionsButtonClicked(): Observable<Void>
  fun reactionsButtonLongPressed(): Observable<Void>
  fun setUserReaction(reaction: String)
  fun setReactions(reactions: List<TopReaction>, nrReactions: Int)
  fun showLoginDialog()
  fun showNetworkErrorToast()
  fun showGenericErrorToast()
}