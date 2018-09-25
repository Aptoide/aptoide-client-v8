package cm.aptoide.pt.comment;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CommentsPresenter implements Presenter {

  private final CommentsView view;
  private final CommentsListManager commentsListManager;
  private final Scheduler viewScheduler;
  private final CrashReport crashReporter;

  public CommentsPresenter(CommentsView view, CommentsListManager commentsListManager,
      Scheduler viewScheduler, CrashReport crashReporter) {
    this.view = view;
    this.commentsListManager = commentsListManager;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
  }

  @Override public void present() {
    showComments();

    pullToRefresh();
  }

  @VisibleForTesting public void pullToRefresh() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes())
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showLoading())
        .flatMapSingle(__ -> commentsListManager.loadFreshComments())
        .observeOn(viewScheduler)
        .doOnNext(comments -> view.hideRefreshLoading())
        .doOnNext(this::showComments)
        .doOnError(throwable -> view.showGeneralError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comments -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void showComments() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> commentsListManager.loadComments())
        .observeOn(viewScheduler)
        .doOnNext(this::showComments)
        .doOnError(throwable -> view.showGeneralError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comments -> {
        }, crashReporter::log);
  }

  private void showComments(List<Comment> comments) {
    view.showComments(comments);
    view.hideLoading();
  }
}
