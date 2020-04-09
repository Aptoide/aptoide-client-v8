package cm.aptoide.pt.editorial.epoxy

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.presenter.EpoxyModelPresenter
import cm.aptoide.pt.presenter.EpoxyView
import cm.aptoide.pt.reactions.ReactionsManager
import cm.aptoide.pt.reactions.network.LoadReactionModel
import cm.aptoide.pt.reactions.network.ReactionsResponse
import rx.Observable
import rx.Scheduler
import rx.Single

class ReactionsModelPresenter(private val reactionsManager: ReactionsManager,
                              private val reactionAnalytics: ReactionAnalytics,
                              private val viewScheduler: Scheduler,
                              private val crashReporter: CrashReport) :
    EpoxyModelPresenter<ReactionsContentModel> {

  lateinit var view: ReactionsContentModel
  lateinit var config: ReactionConfiguration

  override fun present(view: ReactionsContentModel) {
    this.view = view
    loadReactions(config.id, config.groupId)
    handleReactionButtonClick()
    handleReactionButtonLongPress()
    handleUserReactionClick()
  }

  fun loadReactions(cardId: String, groupId: String) {
    view.getLifecycleEvent()
        .filter { lifecycleEvent -> lifecycleEvent == EpoxyView.LifecycleEvent.BIND }
        .flatMapSingle { loadReactionModel(cardId, groupId) }
        .compose(view.bindUntilEvent(EpoxyView.LifecycleEvent.UNBIND))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  fun loadReactionModel(cardId: String, groupId: String): Single<LoadReactionModel> {
    return reactionsManager.loadReactionModel(cardId, groupId)
        .observeOn(viewScheduler)
        .doOnSuccess { reactionModel ->
          view.setUserReaction(reactionModel.myReaction)
          view.setReactions(reactionModel.topReactionList, reactionModel.total)
        }
  }

  private fun handleReactionButtonClick() {
    view.getLifecycleEvent()
        .filter { lifecycleEvent -> lifecycleEvent == EpoxyView.LifecycleEvent.BIND }
        .flatMap { view.reactionsButtonClicked() }
        .flatMap {
          reactionsManager.isFirstReaction(config.id, config.groupId)
              .flatMapObservable { firstReaction ->
                if (firstReaction) {
                  reactionAnalytics.sendReactionButtonClickEvent(config.source)
                  view.showReactionsPopup(config.id, config.groupId)
                  return@flatMapObservable Observable.just(LoadReactionModel())
                } else {
                  return@flatMapObservable reactionsManager.deleteReaction(config.id,
                      config.groupId)
                      .toObservable()
                      .doOnNext { reactionsResponse ->
                        handleReactionsResponse(reactionsResponse, true)
                      }
                      .filter(ReactionsResponse::wasSuccess)
                      .flatMapSingle { loadReactionModel(config.id, config.groupId) }
                }
              }
        }
        .compose(view.bindUntilEvent(EpoxyView.LifecycleEvent.UNBIND))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleReactionButtonLongPress() {
    view.getLifecycleEvent()
        .filter { lifecycleEvent -> lifecycleEvent == EpoxyView.LifecycleEvent.BIND }
        .flatMap { view.reactionsButtonLongPressed() }
        .doOnNext {
          reactionAnalytics.sendReactionButtonClickEvent(config.source)
          view.showReactionsPopup(config.id, config.groupId)
        }
        .compose(view.bindUntilEvent(EpoxyView.LifecycleEvent.UNBIND))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleUserReactionClick() {
    view.getLifecycleEvent()
        .filter { lifecycleEvent -> lifecycleEvent == EpoxyView.LifecycleEvent.BIND }
        .flatMap { view.getReactionEvent() }
        .flatMap { reactionEvent ->
          reactionsManager.setReaction(config.id, config.groupId, reactionEvent.reactionType)
              .toObservable()
              .filter(ReactionsResponse::differentReaction)
              .observeOn(viewScheduler)
              .doOnNext { response -> handleReactionsResponse(response, false) }
              .filter(ReactionsResponse::wasSuccess)
              .flatMapSingle { loadReactionModel(config.id, config.groupId) }

        }
        .compose(view.bindUntilEvent(EpoxyView.LifecycleEvent.UNBIND))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleReactionsResponse(reactionsResponse: ReactionsResponse,
                                      isDelete: Boolean) {
    if (reactionsResponse.wasSuccess()) {
      if (isDelete) {
        reactionAnalytics.sendDeletedEvent(config.source)
      } else {
        reactionAnalytics.sendReactedEvent(config.source)
      }
    } else if (reactionsResponse.reactionsExceeded()) {
      view.showLoginDialog()
    } else if (reactionsResponse.wasNetworkError()) {
      view.showNetworkErrorToast()
    } else if (reactionsResponse.wasGeneralError()) {
      view.showGenericErrorToast()
    }
  }


  fun setConfiguration(reactionConfiguration: ReactionConfiguration) {
    this.config = reactionConfiguration
  }


}