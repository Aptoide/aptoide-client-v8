package cm.aptoide.pt.app.view;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.comments.refactor.CommentsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.editorial.EditorialAnalytics;
import cm.aptoide.pt.editorial.EditorialAppModel;
import cm.aptoide.pt.editorial.EditorialContent;
import cm.aptoide.pt.editorial.EditorialDownloadModel;
import cm.aptoide.pt.editorial.EditorialEvent;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.editorial.EditorialManager;
import cm.aptoide.pt.editorial.EditorialNavigator;
import cm.aptoide.pt.editorial.EditorialPresenter;
import cm.aptoide.pt.editorial.EditorialViewModel;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.ReactionEvent;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 04/10/2018.
 */

public class EditorialPresenterTest {
  private static String GROUP_ID = "CURATION_1";
  @Mock private EditorialFragment view;
  @Mock private EditorialManager editorialManager;
  @Mock private CrashReport crashReport;
  @Mock private PermissionManager permissionManager;
  @Mock private PermissionService permissionService;
  @Mock private EditorialAnalytics editorialAnalytics;
  @Mock private EditorialNavigator editorialNavigator;
  @Mock private CommentsManager commentsManager;

  private EditorialPresenter presenter;
  private EditorialViewModel editorialViewModel;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private EditorialDownloadModel downloadModel;
  private EditorialViewModel errorEditorialViewModel;
  private EditorialViewModel loadingEditorialViewModel;
  private ArrayList<EditorialContent> editorialContent;
  private PublishSubject<Void> reactionButtonClickEvent;
  private PublishSubject<Void> reactionButtonLongPressEvent;
  private PublishSubject<ReactionEvent> reactionClickEvent;
  private PublishSubject<Void> snackLoginEvent;

  @Before public void setupEditorialPresenter() {
    MockitoAnnotations.initMocks(this);
    presenter = new EditorialPresenter(view, editorialManager, Schedulers.immediate(), crashReport,
        permissionManager, permissionService, editorialAnalytics, editorialNavigator,
        commentsManager);
    lifecycleEvent = PublishSubject.create();
    reactionButtonClickEvent = PublishSubject.create();
    reactionButtonLongPressEvent = PublishSubject.create();
    reactionClickEvent = PublishSubject.create();
    snackLoginEvent = PublishSubject.create();
    editorialContent = new ArrayList<>();
    editorialContent.add(
        new EditorialContent("title", Collections.emptyList(), "message", "type", 1,
            new EditorialAppModel(1, "appName", "icon", 1f, "packageName", 0, "graphic", null, 1,
                "storeName", "verName", 0, "path", "pathAlt", "md5", Collections.emptyList(),
                Collections.emptyList(), false, "")));
    editorialViewModel = new EditorialViewModel(editorialContent, "title", "caption", "background",
        Collections.emptyList(), editorialContent, false, "1", "CURATION_1", "");
    downloadModel = new EditorialDownloadModel(DownloadModel.Action.INSTALL, 0,
        DownloadModel.DownloadState.ACTIVE, 1);
    errorEditorialViewModel = new EditorialViewModel(EditorialViewModel.Error.GENERIC);
    loadingEditorialViewModel = new EditorialViewModel(true);
    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.snackLoginClick()).thenReturn(snackLoginEvent);
  }

  @Test public void onCreateLoadAppOfTheWeekWithCorrectViewModelTest() {
    //Given an initialized presenter
    presenter.firstLoad();
    //When the view Model is requested the editorialViewModel should be returned
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should show loading at the start
    verify(view).showLoading();
    //Hide it when the view model is received
    verify(view).hideLoading();
    //And then populate the view
    verify(view).populateView(editorialViewModel);
    //And signal that it's ready to download
  }

  @Test public void onCreateLoadAppOfTheWeekWithErrorViewModelTest() {
    //Given an initialized presenter
    presenter.firstLoad();
    //When the view Model is requested the editorialViewModel should be returned
    when(editorialManager.loadEditorialViewModel()).thenReturn(
        Single.just(errorEditorialViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should show loading at the start
    verify(view).showLoading();
    //Hide it when the view model is received
    verify(view).hideLoading();
    //And then show the error
    verify(view).showError(EditorialViewModel.Error.GENERIC);
  }

  @Test public void onCreateLoadAppOfTheWeekWithLoadingViewModelTest() {
    //Given an initialized presenter
    presenter.firstLoad();

    //When the view Model is requested the editorialViewModel should be returned
    when(editorialManager.loadEditorialViewModel()).thenReturn(
        Single.just(loadingEditorialViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should show loading at the start
    verify(view).showLoading();
    //And it should continue to show the loading
    verify(view, never()).hideLoading();
  }

  @Test public void handleRetryClickTest() {
    //Given an initialized presenter
    presenter.handleRetryClick();

    //When the user clicks on the retry button
    when(view.retryClicked()).thenReturn(Observable.just(null));

    //When the view Model is requested the editorialViewModel should be returned
    when(editorialManager.loadEditorialViewModel()).thenReturn(
        Single.just(errorEditorialViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should show loading at the start
    verify(view).showLoading();
    //And then do the normal behaviour when creating the view
    view.populateView(editorialViewModel);
  }

  @Test public void handleClickOnMediaTest() {
    //Given an initialized presenter
    presenter.handleClickOnMedia();

    //When the user clicks on a media
    when(view.mediaContentClicked()).thenReturn(
        Observable.just(new EditorialEvent(EditorialEvent.Type.MEDIA, "url")));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the url of the media
    verify(editorialNavigator).navigateToUri("url");
  }

  @Test public void handleClickActionButtonTest() {
    //Given an initialized presenter
    presenter.handleClickActionButtonCard();

    //When the user clicks on a media
    when(view.actionButtonClicked()).thenReturn(
        Observable.just(new EditorialEvent(EditorialEvent.Type.ACTION, "url")));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the url of the media
    verify(editorialNavigator).navigateToUri("url");
  }

  @Test public void loadDownloadAppTest() {
    //Given an initialized presenter
    presenter.firstLoad();
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    //And request and load the downloadModel for each one
    when(editorialManager.loadDownloadModel("md5", "packageName", 0, editorialContent.get(0)
        .getPosition())).thenReturn(Observable.just(downloadModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver the download model to the view
    verify(view).populateView(editorialViewModel);
  }

  @Test public void handlePlaceHolderVisibilityChangeRemoveTest() {
    //Given an initialized presenter
    presenter.handleBottomCardVisibilityChange();

    when(view.bottomCardVisibilityChange()).thenReturn(Observable.just(false));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the bottom appCard and show the placeHolder appCard
    verify(view).removeBottomCardAnimation();
  }

  @Test public void handlePlaceHolderVisibilityChangeAddTest() {
    //Given an initialized presenter
    presenter.handleBottomCardVisibilityChange();

    when(view.bottomCardVisibilityChange()).thenReturn(Observable.just(true));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the placeHolder appCard and show the bottom appCard
    verify(view).addBottomCardAnimation();
  }
}
