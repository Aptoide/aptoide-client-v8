package cm.aptoide.pt.comments.refactor

import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.presenter.ViewPresenter
import rx.Scheduler

class CommentsPresenter(private val accountManager: AptoideAccountManager,
                        private val commentsManager: CommentsManager,
                        private val viewScheduler: Scheduler,
                        private val crashReporter: CrashReport) :
    ViewPresenter<CommentsViewI, CommentsConfiguration>() {

  override fun present() {
    commentsManager.setConfiguration(configuration)
    loadComments()
  }

  private fun loadComments() {
    lifecycleView.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMapSingle { commentsManager.loadComments() }
        .observeOn(viewScheduler)
        .doOnNext { response -> view.populateComments(response) }
        .compose(lifecycleView.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }
}