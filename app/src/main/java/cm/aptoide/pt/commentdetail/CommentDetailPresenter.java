package cm.aptoide.pt.commentdetail;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CommentDetailPresenter implements Presenter {

  private final CommentDetailView view;
  private final CommentDetailManager commentManager;
  private final Scheduler viewScheduler;

  public CommentDetailPresenter(CommentDetailView view, CommentDetailManager commentManager,
      Scheduler viewScheduler) {
    this.view = view;
    this.commentManager = commentManager;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    showCommentViewModel();
  }

  @VisibleForTesting public void showCommentViewModel() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> commentManager.loadCommentModel())
        .observeOn(viewScheduler)
        .doOnNext(this::showComment)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(commentDetailViewModel -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void showComment(CommentDetailViewModel commentViewModel) {
    view.showCommentModel(commentViewModel);
    view.hideLoading();
  }
}
