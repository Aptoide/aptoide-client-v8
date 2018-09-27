package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentDetailPresenterTest {

  @Mock private CommentDetailFragment view;
  @Mock private CommentDetailManager commentDetailManager;

  private CommentDetailPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();

    presenter = new CommentDetailPresenter(view, commentDetailManager, Schedulers.immediate());

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
  }

  @Test public void showCommentViewModelTest() {
    when(commentDetailManager.loadCommentModel()).thenReturn(
        Single.just(new CommentDetailViewModel()));
    //Given an initialized presenter
    presenter.showCommentViewModel();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentDetailManager).loadCommentModel();
    //Then the loading should be hidden
    verify(view).hideLoading();
  }
}