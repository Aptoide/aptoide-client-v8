package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.mock.FakeCommentsDataSource;
import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentsListManagerPresenterTest {

  @Mock private CommentsFragment view;
  @Mock private CommentsListManager commentsListManager;

  private CommentsPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    presenter = new CommentsPresenter(view, commentsListManager, Schedulers.immediate());

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void showCommentsTest() {
    when(commentsListManager.loadComments()).thenReturn(
        new FakeCommentsDataSource().loadComments(storeId));
    //Given an initialized CommentsPresenter
    presenter.present();
    //When the the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).hideLoading();
  }
}