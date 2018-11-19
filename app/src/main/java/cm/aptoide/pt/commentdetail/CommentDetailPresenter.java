package cm.aptoide.pt.commentdetail;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.comment.CommentsNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CommentDetailPresenter implements Presenter {

  private final CommentDetailView view;
  private final CommentDetailManager commentManager;
  private final Scheduler viewScheduler;
  private final AptoideAccountManager accountManager;
  private CommentsNavigator commentsNavigator;

  public CommentDetailPresenter(CommentDetailView view, CommentDetailManager commentManager,
      Scheduler viewScheduler, AptoideAccountManager accountManager,
      CommentsNavigator commentsNavigator) {
    this.view = view;
    this.commentManager = commentManager;
    this.viewScheduler = viewScheduler;
    this.accountManager = accountManager;
    this.commentsNavigator = commentsNavigator;
  }

  @Override public void present() {
    showCommentViewModel();
    postComment();
    handleClickOnUser();
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

  @VisibleForTesting public void postComment() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.commentClicked()
            .doOnNext(__ -> view.hideKeyboard())
            .flatMap(comment -> accountManager.accountStatus()
                .map(account -> {
                  if (account.isLoggedIn()) {
                    return account;
                  } else {
                    return null;
                  }
                })
                .filter(account -> account != null)
                .observeOn(viewScheduler)
                .flatMapCompletable(account -> commentManager.replyComment(comment)
                    .observeOn(viewScheduler)
                    .doOnCompleted(() -> view.addLocalComment(comment, account)))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleClickOnUser() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.userClickEvent())
        .doOnNext(id -> commentsNavigator.navigateToStore(id))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void showComment(CommentDetailViewModel commentViewModel) {
    view.showCommentModel(commentViewModel);
    view.hideLoading();
  }
}
