package cm.aptoide.pt.app.view;

import android.graphics.Color;
import android.support.v7.graphics.Palette;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  @Mock private EditorialFragment view;
  @Mock private EditorialManager editorialManager;
  @Mock private CrashReport crashReport;
  @Mock private PermissionManager permissionManager;
  @Mock private PermissionService permissionService;
  @Mock private EditorialAnalytics editorialAnalytics;
  @Mock private EditorialNavigator editorialNavigator;

  private EditorialPresenter editorialPresenter;
  private EditorialViewModel editorialViewModel;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private DownloadModel downloadModel;
  private EditorialViewModel errorEditorialViewModel;
  private EditorialViewModel loadingEditorialViewModel;

  @Before public void setupEditorialPresenter() {
    MockitoAnnotations.initMocks(this);
    editorialPresenter =
        new EditorialPresenter(view, editorialManager, Schedulers.immediate(), crashReport,
            permissionManager, permissionService, editorialAnalytics, editorialNavigator);
    lifecycleEvent = PublishSubject.create();
    List<EditorialContent> editorialContent = new ArrayList<>();
    editorialContent.add(
        new EditorialContent("title", Collections.emptyList(), "message", "type", "appName", "icon",
            0));
    editorialViewModel =
        new EditorialViewModel(editorialContent, "type", "title", 1, "caption", "appName", 0,
            "packageName", 0, "icon", "graphic", null, 0, "storeName", "storeTheme", "versionName",
            0, "path", "backgroundImage", "pathAlt", "md5", 0);
    downloadModel =
        new DownloadModel(DownloadModel.Action.INSTALL, 0, DownloadModel.DownloadState.ACTIVE,
            null);
    errorEditorialViewModel = new EditorialViewModel(EditorialViewModel.Error.GENERIC);
    loadingEditorialViewModel = new EditorialViewModel(true);
    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
  }

  @Test public void onCreateLoadAppOfTheWeekWithCorrectViewModelTest() {
    //Given an initialized presenter
    editorialPresenter.onCreateLoadAppOfTheWeek();
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
    verify(view).readyToDownload();
  }

  @Test public void onCreateLoadAppOfTheWeekWithErrorViewModelTest() {
    //Given an initialized presenter
    editorialPresenter.onCreateLoadAppOfTheWeek();
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
    editorialPresenter.onCreateLoadAppOfTheWeek();

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
    editorialPresenter.handleRetryClick();

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
    view.readyToDownload();
  }

  @Test public void handleClickOnMediaTest() {
    //Given an initialized presenter
    editorialPresenter.handleClickOnMedia();

    //When the user clicks on a media
    when(view.mediaContentClicked()).thenReturn(
        Observable.just(new EditorialEvent(EditorialEvent.Type.MEDIA, "url")));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate to the url of the media
    verify(editorialNavigator).navigateToUri("url");
  }

  @Test public void handleClickOnAppCardTest() {
    //Given an initialized presenter
    editorialPresenter.handleClickOnAppCard();

    //When the user clicks on an appCard
    when(view.appCardClicked()).thenReturn(
        Observable.just(new EditorialEvent(EditorialEvent.Type.APPCARD)));

    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Finally it should navigate to the appView of the app of the card
    verify(editorialNavigator).navigateToAppView(1, "packageName");
  }

  @Test public void loadDownloadAppTest() {
    //Given an initialized presenter
    editorialPresenter.loadDownloadApp();

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    //And request and load the downloadModel
    when(editorialManager.loadDownloadModel("md5", "packageName", 0, false, null)).thenReturn(
        Observable.just(downloadModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver the download model to the view
    verify(view).showDownloadModel(downloadModel);
  }

  @Test public void handlePlaceHolderVisibilityTest() {
    //Given an initialized presenter
    editorialPresenter.handlePlaceHolderVisibility();

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the view should handle the placeHolder visibility changes
    verify(view).managePlaceHolderVisibity();
  }

  @Test public void handlePlaceHolderVisibilityChangeRemoveTest() {
    //Given an initialized presenter
    editorialPresenter.handlePlaceHolderVisibilityChange();

    //when there's a scroll event where the placeholder changes visibility
    when(view.placeHolderVisibilityChange()).thenReturn(
        Observable.just(new ScrollEvent(true, true)));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the bottom appCard and show the placeHolder appCard
    verify(view).removeBottomCardAnimation();
  }

  @Test public void handlePlaceHolderVisibilityChangeAddTest() {
    //Given an initialized presenter
    editorialPresenter.handlePlaceHolderVisibilityChange();

    //when there's a scroll event where the placeholder changes visibility
    when(view.placeHolderVisibilityChange()).thenReturn(
        Observable.just(new ScrollEvent(false, false)));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the placeHolder appCard and show the bottom appCard
    verify(view).addBottomCardAnimation();
  }

  @Test public void handlePaletterColorPickTest() {
    Palette.Swatch swatch = new Palette.Swatch(Color.RED, 256);
    //Given an initialized presenter
    editorialPresenter.handlePaletteColor();

    //when the palette swatch is extracted
    when(view.paletteSwatchExtracted()).thenReturn(
        Observable.just(new Palette.Swatch(Color.RED, 256)));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver that swatch to the view
    verify(view).applyPaletteSwatch(swatch);
  }

  @Test public void handleMediaListDescriptionVisibilityOnlyOneMediaVisibleTest() {
    //Given an initialized presenter
    editorialPresenter.handleMediaListDescriptionVisibility();
    EditorialEvent editorialEvent =
        new EditorialEvent(EditorialEvent.Type.MEDIA_LIST, 1, 1, 3, Collections.emptyList());
    //When the mediaList description is changes, then an event with the first and last item position of that list, the position of the viewHolder, and a list of the media should be returned
    when(view.mediaListDescriptionChanged()).thenReturn(Observable.just(editorialEvent));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver that swatch to the view
    verify(view).manageMediaListDescriptionAnimationVisibility(editorialEvent);
  }

  @Test public void handleMediaListDescriptionVisibilityMoreThanOneMediaVisibleTest() {
    //Given an initialized presenter
    editorialPresenter.handleMediaListDescriptionVisibility();
    EditorialEvent editorialEvent =
        new EditorialEvent(EditorialEvent.Type.MEDIA_LIST, 1, 3, 3, Collections.emptyList());
    //When the mediaList description is changes, then an event with the first and last item position of that list, the position of the viewHolder, and a list of the media should be returned
    when(view.mediaListDescriptionChanged()).thenReturn(Observable.just(editorialEvent));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver that swatch to the view
    verify(view).setMediaListDescriptionsVisible(editorialEvent);
  }
}
