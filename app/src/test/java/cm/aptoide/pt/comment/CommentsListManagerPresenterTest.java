package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.mock.FakeCommentsDataSource;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentsListManagerPresenterTest {

  @Mock private CommentsFragment view;
  @Mock private CommentsListManager commentsListManager;
  @Mock private CrashReport crashReporter;
  @Mock private CommentsNavigator commentsNavigator;

  private CommentsPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Void> pullToRefreshEvent;
  private PublishSubject<Comment> commentClickEvent;
  private PublishSubject<Comment> commentPostEvent;
  private FakeCommentsDataSource fakeCommentsDataSource;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    pullToRefreshEvent = PublishSubject.create();
    commentClickEvent = PublishSubject.create();
    commentPostEvent = PublishSubject.create();

    presenter =
        new CommentsPresenter(view, commentsListManager, commentsNavigator, Schedulers.immediate(),
            crashReporter);
    fakeCommentsDataSource = new FakeCommentsDataSource();

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.refreshes()).thenReturn(pullToRefreshEvent);
    when(view.commentClick()).thenReturn(commentClickEvent);
    when(view.commentPost()).thenReturn(commentPostEvent);
  }

  @Test public void showCommentsTest() {
    when(commentsListManager.loadComments()).thenReturn(
        fakeCommentsDataSource.loadComments(15, false)
            .map(commentsResponseModel -> new CommentsListViewModel("",
                commentsResponseModel.getComments(), false)));
    //Given an initialized CommentsPresenter
    presenter.showComments();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).hideLoading();
  }

  @Test public void showErrorIfCommentsFail() {
    when(commentsListManager.loadComments()).thenReturn(
        Single.error(new IllegalStateException("test")));
    //Given an initialized CommentsPresenter
    presenter.showComments();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).showGeneralError();
  }

  @Test public void pullRefreshTest() {
    CommentsListViewModel commentsViewModel = fakeCommentsDataSource.loadComments(15, false)
        .map(commentsResponseModel -> new CommentsListViewModel("",
            commentsResponseModel.getComments(), false))
        .toBlocking()
        .value();

    when(commentsListManager.loadFreshComments()).thenReturn(Single.just(commentsViewModel));
    //Given an initialized CommentsPresenter
    presenter.pullToRefresh();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And the user makes a pull to refresh action
    pullToRefreshEvent.onNext(null);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the fresh comments should be requests
    verify(commentsListManager).loadFreshComments();
    //Then the comments should be shown
    verify(view).showComments(commentsViewModel);
  }

  @Test public void commentClick() {
    //Given an initialized CommentsPresenter
    presenter.clickComment();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And a comment is clicked
    Comment comment = new Comment();
    commentClickEvent.onNext(comment);
    //Then navigation to the comment detail should start
    verify(commentsNavigator).navigateToCommentView(comment);
  }

  @Test public void postComment() {
    //Given an initialized CommentsPresenter
    presenter.postComment();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And a comment is written
    Comment comment = new Comment();
    when(commentsListManager.postComment(comment)).thenReturn(Completable.complete());
    //And the send action button is clicked
    commentPostEvent.onNext(comment);
    //Then the comment should be sent to the repository
    verify(commentsListManager).postComment(comment);
  }
}