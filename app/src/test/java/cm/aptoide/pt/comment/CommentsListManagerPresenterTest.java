package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.mock.FakeCommentsDataSource;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentsListManagerPresenterTest {

  @Mock private CommentsFragment view;
  @Mock private CommentsListManager commentsListManager;

  private CommentsPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Void> pullToRefreshEvent;
  private FakeCommentsDataSource fakeCommentsDataSource;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    pullToRefreshEvent = PublishSubject.create();

    presenter = new CommentsPresenter(view, commentsListManager, Schedulers.immediate());
    fakeCommentsDataSource = new FakeCommentsDataSource();

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.refreshes()).thenReturn(pullToRefreshEvent);
  }

  @Test public void showCommentsTest() {
    when(commentsListManager.loadComments()).thenReturn(fakeCommentsDataSource.loadComments(15));
    //Given an initialized CommentsPresenter
    presenter.present();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).hideLoading();
  }

  @Test public void pullRefreshTest() {
    Single<List<Comment>> value = fakeCommentsDataSource.loadComments(15);
    when(commentsListManager.loadFreshComments()).thenReturn(value);
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
    verify(view).showComments(value.toBlocking()
        .value());
  }
}