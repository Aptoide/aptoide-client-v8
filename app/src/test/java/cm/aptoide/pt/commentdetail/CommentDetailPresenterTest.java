package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.comment.CommentDetailResponseModel;
import cm.aptoide.pt.comment.mock.FakeCommentsDataSource;
import cm.aptoide.pt.presenter.View;
import java.util.Date;
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
  private FakeCommentsDataSource fakeCommentsDataSource;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();

    presenter = new CommentDetailPresenter(view, commentDetailManager, Schedulers.immediate());
    fakeCommentsDataSource = new FakeCommentsDataSource();

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
  }

  @Test public void showCommentViewModelTest() {
    CommentDetailResponseModel dataModelResponse = fakeCommentsDataSource.loadComment(0)
        .toBlocking()
        .value();

    CommentDetailViewModel viewModel =
        new CommentDetailViewModel("Filipe Gonçalves", "http://via.placeholder.com/350x150",
            "Eu sou do Benfica", "http://via.placeholder.com/350x150", 7, new Date(),
            dataModelResponse.getReplies());

    when(commentDetailManager.loadCommentModel()).thenReturn(Single.just(viewModel));

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
    //Then the comments view model should be shown in the view
    verify(view).showCommentModel(viewModel);
  }
}