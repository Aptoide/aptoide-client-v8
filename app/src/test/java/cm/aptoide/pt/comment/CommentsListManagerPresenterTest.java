package cm.aptoide.pt.comment;

import cm.aptoide.pt.presenter.View;
import java.util.Arrays;
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

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    presenter = new CommentsPresenter(view, commentsListManager, Schedulers.immediate());

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void showCommentsTest() {
    when(commentsListManager.loadComments()).thenReturn(
        Single.just(Arrays.asList("comment1", "comment2", "comment2")));
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