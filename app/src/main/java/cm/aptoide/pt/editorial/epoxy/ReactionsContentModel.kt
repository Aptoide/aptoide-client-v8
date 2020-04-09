package cm.aptoide.pt.editorial.epoxy

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.aptoideviews.safeLet
import cm.aptoide.pt.R
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.presenter.EpoxyModelView
import cm.aptoide.pt.reactions.ReactionEvent
import cm.aptoide.pt.reactions.ReactionMapper
import cm.aptoide.pt.reactions.data.ReactionType
import cm.aptoide.pt.reactions.data.TopReaction
import cm.aptoide.pt.reactions.ui.ReactionsPopup
import cm.aptoide.pt.themes.ThemeManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding.view.RxView
import rx.Observable
import rx.subjects.PublishSubject


@EpoxyModelClass(layout = R.layout.editorial_reactions_layout)
abstract class ReactionsContentModel : EpoxyModelView<ReactionsContentModel.CardHolder>(),
    ReactionView {
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var themeManager: ThemeManager? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var presenter: ReactionsModelPresenter? = null

  @EpoxyAttribute
  var reactionConfiguration: ReactionConfiguration? = null

  var reactionsPopup: ReactionsPopup? = null
  var holder: CardHolder? = null

  private var reactionEventListener: PublishSubject<ReactionEvent> = PublishSubject.create()

  override fun bind(holder: CardHolder) {
    this.holder = holder
    safeLet(presenter, reactionConfiguration) { p, config ->
      p.setConfiguration(config)
      attachPresenter(p, this)
    }
    super.bind(holder)
    reactionsPopup = ReactionsPopup(holder.itemView.context, holder.reactButton)
  }

  override fun unbind(holder: CardHolder) {
    super.unbind(holder)
    this.holder = null
    this.reactionsPopup = null
  }

  override fun setUserReaction(reaction: String) {
    holder?.let { holder ->
      if (isReactionValid(reaction)) {
        holder.reactButton.setImageResource(ReactionMapper.mapReaction(reaction))
      } else {
        themeManager?.let { tm ->
          holder.reactButton.setImageResource(
              tm.getAttributeForTheme(R.attr.reactionInputDrawable).resourceId)
        }
      }
    }
  }

  override fun setReactions(r: List<TopReaction>, nr: Int) {
    holder?.let { holder ->
      var validReactions = 0
      for (i in holder.imageViews.indices) {
        if (i < r.size && isReactionValid(r[i].type)) {
          ImageLoader.with(holder.itemView.context)
              .loadWithShadowCircleTransform(ReactionMapper.mapReaction(r[i]
                  .type), holder.imageViews[i])
          holder.imageViews[i].visibility = View.VISIBLE
          validReactions++
        } else {
          holder.imageViews[i].visibility = View.GONE
        }
      }
      if (nr > 0 && validReactions > 0) {
        holder.numberOfReactions.text = nr.toString()
        holder.numberOfReactions.visibility = View.VISIBLE
      } else {
        holder.numberOfReactions.visibility = View.GONE
      }
    }
  }

  fun showReactionsPopup(cardId: String?, groupId: String?) {
    reactionsPopup?.show()
    reactionsPopup?.setOnReactionsItemClickListener { item: ReactionType? ->
      reactionEventListener.onNext(
          ReactionEvent(cardId, ReactionMapper.mapUserReaction(item), groupId))
      reactionsPopup?.dismiss()
      reactionsPopup?.setOnReactionsItemClickListener(null)
    }
  }

  fun isReactionValid(reaction: String): Boolean {
    return reaction != "" && ReactionMapper.mapReaction(reaction) != -1
  }


  override fun getReactionEvent(): Observable<ReactionEvent> {
    return reactionEventListener.asObservable()
  }

  override fun reactionsButtonClicked(): Observable<Void> {
    return RxView.clicks(holder!!.reactButton)
  }

  override fun reactionsButtonLongPressed(): Observable<Void> {
    return RxView.longClicks(holder!!.reactButton)
  }

  override fun showLoginDialog() {
    holder?.itemView?.let { v ->
      Snackbar.make(v, v.context.getText(R.string.editorial_reactions_login_short),
          Snackbar.LENGTH_LONG).show()
    }
  }

  override fun showNetworkErrorToast() {
    holder?.itemView?.let { v ->
      Snackbar.make(v, v.context.getText(R.string.connection_error),
          Snackbar.LENGTH_LONG).show()
    }
  }

  override fun showGenericErrorToast() {
    holder?.itemView?.let { v ->
      Snackbar.make(v, v.context.getText(R.string.error_occured),
          Snackbar.LENGTH_LONG).show()
    }
  }

  class CardHolder : BaseViewHolder() {
    val firstReaction by bind<ImageView>(R.id.reaction_1)
    val secondReaction by bind<ImageView>(R.id.reaction_2)
    val thirdReaction by bind<ImageView>(R.id.reaction_3)
    val reactButton by bind<ImageButton>(R.id.add_reactions)
    val numberOfReactions by bind<TextView>(R.id.number_of_reactions)
    lateinit var imageViews: Array<ImageView>

    override fun bindView(itemView: View) {
      super.bindView(itemView)
      imageViews = arrayOf(firstReaction, secondReaction, thirdReaction)
    }
  }
}