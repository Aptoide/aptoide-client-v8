package cm.aptoide.pt.comment;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CommentsPresenter implements Presenter {

  private final CommentsView view;
  private final CommentsListManager commentsListManager;
  private final Scheduler viewScheduler;

  public CommentsPresenter(CommentsView view, CommentsListManager commentsListManager,
      Scheduler viewScheduler) {
    this.view = view;
    this.commentsListManager = commentsListManager;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    showComments();
  }

  private void showComments() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> commentsListManager.loadComments())
        .observeOn(viewScheduler)
        .doOnNext(comments -> {
          view.showComments(comments);
          view.hideLoading();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comments -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
