package cm.aptoide.pt.app.view;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.editorial.EditorialAnalytics;
import cm.aptoide.pt.editorial.EditorialContent;
import cm.aptoide.pt.editorial.EditorialDownloadModel;
import cm.aptoide.pt.editorial.EditorialEvent;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.editorial.EditorialManager;
import cm.aptoide.pt.editorial.EditorialNavigator;
import cm.aptoide.pt.editorial.EditorialPresenter;
import cm.aptoide.pt.editorial.EditorialViewModel;
import cm.aptoide.pt.editorial.ScrollEvent;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.ReactionEvent;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
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
import static org.mockito.Mockito.times;
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
        permissionManager, permissionService, editorialAnalytics, editorialNavigator);
    lifecycleEvent = PublishSubject.create();
    reactionButtonClickEvent = PublishSubject.create();
    reactionButtonLongPressEvent = PublishSubject.create();
    reactionClickEvent = PublishSubject.create();
    snackLoginEvent = PublishSubject.create();
    editorialContent = new ArrayList<>();
    editorialContent.add(
        new EditorialContent("title", Collections.emptyList(), "message", "type", 1, "appName",
            "icon", 1, "packageName", 0, "graphic", null, 1, "storeName", "verName", 0, "path",
            "pathAlt", "md5", "actionTitle", "url", 1));
    editorialViewModel = new EditorialViewModel(editorialContent, "title", "caption", "background",
        Collections.emptyList(), editorialContent, false, "1", "CURATION_1");
    downloadModel = new EditorialDownloadModel(DownloadModel.Action.INSTALL, 0,
        DownloadModel.DownloadState.ACTIVE, null, 1);
    errorEditorialViewModel = new EditorialViewModel(EditorialViewModel.Error.GENERIC);
    loadingEditorialViewModel = new EditorialViewModel(true);
    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.reactionsButtonClicked()).thenReturn(reactionButtonClickEvent);
    when(view.reactionsButtonLongPressed()).thenReturn(reactionButtonLongPressEvent);
    when(view.reactionClicked()).thenReturn(reactionClickEvent);
    when(view.snackLogInClick()).thenReturn(snackLoginEvent);
  }

  @Test public void onCreateLoadAppOfTheWeekWithCorrectViewModelTest() {
    //Given an initialized presenter
    presenter.onCreateLoadAppOfTheWeek();
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
    presenter.onCreateLoadAppOfTheWeek();
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
    presenter.onCreateLoadAppOfTheWeek();

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

  @Test public void handleClickOnAppCardTest() {
    //Given an initialized presenter
    presenter.handleClickOnAppCard();

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    //When the user clicks on an appCard
    when(view.appCardClicked(editorialViewModel)).thenReturn(
        Observable.just(new EditorialEvent(EditorialEvent.Type.APPCARD, 1, "packageName")));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Finally it should navigate to the appView of the app of the card
    verify(editorialNavigator).navigateToAppView(1, "packageName");
  }

  @Test public void loadDownloadAppTest() {
    //Given an initialized presenter
    presenter.loadDownloadApp();

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));

    //And request and load the downloadModel for each one
    when(editorialManager.loadDownloadModel("md5", "packageName", 0, false, null,
        editorialContent.get(0)
            .getPosition())).thenReturn(Observable.just(downloadModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver the download model to the view
    verify(view).showDownloadModel(downloadModel);
  }

  @Test public void handlePlaceHolderVisibilityTest() {
    //Given an initialized presenter
    presenter.handlePlaceHolderVisibility();

    //When the view is ready
    when(view.isViewReady()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the view should handle the placeHolder visibility changes
    verify(view).managePlaceHolderVisibity();
  }

  @Test public void handlePlaceHolderVisibilityChangeRemoveTest() {
    //Given an initialized presenter
    presenter.handlePlaceHolderVisibilityChange();

    //when there's a scroll event where the placeholder changes visibility
    when(view.placeHolderVisibilityChange()).thenReturn(
        Observable.just(new ScrollEvent(true, true)));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the bottom appCard and show the placeHolder appCard
    verify(view).removeBottomCardAnimation();
  }

  @Test public void handlePlaceHolderVisibilityChangeAddTest() {
    //Given an initialized presenter
    presenter.handlePlaceHolderVisibilityChange();

    //when there's a scroll event where the placeholder changes visibility
    when(view.placeHolderVisibilityChange()).thenReturn(
        Observable.just(new ScrollEvent(false, false)));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the placeHolder appCard and show the bottom appCard
    verify(view).addBottomCardAnimation();
  }

  @Test public void handleMediaListDescriptionVisibilityOnlyOneMediaVisibleTest() {
    //Given an initialized presenter
    presenter.handleMediaListDescriptionVisibility();
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
    presenter.handleMediaListDescriptionVisibility();
    EditorialEvent editorialEvent =
        new EditorialEvent(EditorialEvent.Type.MEDIA_LIST, 1, 3, 3, Collections.emptyList());
    //When the mediaList description is changes, then an event with the first and last item position of that list, the position of the viewHolder, and a list of the media should be returned
    when(view.mediaListDescriptionChanged()).thenReturn(Observable.just(editorialEvent));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should deliver that swatch to the view
    verify(view).setMediaListDescriptionsVisible(editorialEvent);
  }

  @Test public void handleMovingCollapseVisiblePlaceHolderTest() {
    //Given an initialized presenter
    presenter.handleMovingCollapse();
    //If item is shown when collapse toolbar is moving
    when(view.handleMovingCollapse()).thenReturn(Observable.just(true));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should remove the bottom app card
    verify(view).removeBottomCardAnimation();
  }

  @Test public void handleMovingCollapseNotVisiblePlaceHolderTest() {
    //Given an initialized presenter
    presenter.handleMovingCollapse();
    //If item is shown when collapse toolbar is moving
    when(view.handleMovingCollapse()).thenReturn(Observable.just(false));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should add the bottom app card
    verify(view).addBottomCardAnimation();
  }

  @Test public void handleReactionButtonClickFirstReactionTest() {
    //Given an initialised presenter
    presenter.handleReactionButtonClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And it's the first time the user is reacting to that card
    when(editorialManager.isFirstReaction("1", GROUP_ID)).thenReturn(true);
    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));
    //The user clicks the reaction button
    reactionButtonClickEvent.onNext(null);
    //It should send the corresponding analytic and show the reactions pop up
    verify(editorialAnalytics).sendReactionButtonClickEvent();
    verify(view).showReactionsPopup("1", GROUP_ID);
  }

  @Test public void handleReactionButtonClickSecondReactionTest() {
    //Given an initialised presenter
    presenter.handleReactionButtonClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And the user has a reaction already submitted on that card
    when(editorialManager.isFirstReaction("1", GROUP_ID)).thenReturn(false);
    when(editorialManager.deleteReaction("1", GROUP_ID)).thenReturn(
        Single.just(new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SUCCESS)));
    when(editorialManager.loadReactionModel("1", GROUP_ID)).thenReturn(
        Single.just(new LoadReactionModel(10, "myReaction", "userId", Collections.emptyList())));
    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));
    //The user clicks the reaction button
    reactionButtonClickEvent.onNext(null);
    //It should request the deletion of the reaction
    verify(editorialManager).deleteReaction("1", GROUP_ID);
    //It should send the corresponding analytic and load the reactions and update the corresponding card
    verify(editorialAnalytics).sendDeletedEvent();
    verify(editorialManager).loadReactionModel("1", GROUP_ID);
    verify(view).setReactions("myReaction", Collections.emptyList(), 10);
  }

  @Test public void handleReactionButtonLongPressTest() {
    //Given an initialised presenter
    presenter.handleLongPressReactionButton();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));
    //The user long presses the reaction button
    reactionButtonLongPressEvent.onNext(null);
    //It should send the corresponding analytic and show the reactions pop up
    verify(editorialAnalytics).sendReactionButtonClickEvent();
    verify(view).showReactionsPopup("1", GROUP_ID);
  }

  @Test public void handleUserReactionTest() {
    //Given an initialised presenter
    presenter.handleUserReaction();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should request to set said reaction
    when(editorialManager.setReaction("1", GROUP_ID, "laugh")).thenReturn(
        Single.just(new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SUCCESS)));
    when(editorialManager.loadReactionModel("1", GROUP_ID)).thenReturn(
        Single.just(new LoadReactionModel(10, "myReaction", "userId", Collections.emptyList())));
    //Then it should request and load the editorialViewModel
    when(editorialManager.loadEditorialViewModel()).thenReturn(Single.just(editorialViewModel));
    //The user chooses a reaction
    reactionClickEvent.onNext(new ReactionEvent("1", "laugh", GROUP_ID));
    //It should send the corresponding analytic and load the reactions and update the corresponding card
    verify(editorialAnalytics).sendReactedEvent();
    verify(editorialManager).loadReactionModel("1", GROUP_ID);
    verify(view).setReactions("myReaction", Collections.emptyList(), 10);
  }

  @Test public void handleUserReactionWithSameReactionTest() {
    //Given an initialised presenter
    presenter.handleUserReaction();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //The user chooses a reaction
    reactionClickEvent.onNext(new ReactionEvent("1", "laugh", GROUP_ID));
    //It should request to set said reaction
    when(editorialManager.setReaction("1", GROUP_ID, "laugh")).thenReturn(Single.just(
        new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SAME_REACTION)));
    //It should send the corresponding analytic and load the reactions and update the corresponding card
    verify(editorialAnalytics, times(0)).sendReactedEvent();
    when(editorialManager.loadReactionModel("1", GROUP_ID)).thenReturn(
        Single.just(new LoadReactionModel(10, "myReaction", "userId", Collections.emptyList())));
    verify(editorialManager, times(0)).loadReactionModel("1", GROUP_ID);
    verify(view, times(0)).setReactions("myReaction", Collections.emptyList(), 10);
  }

  @Test public void handleSnackLogInTest() {
    //Given an initialised presenter
    presenter.handleSnackLogInClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //The user clicks on the log in button
    snackLoginEvent.onNext(null);
    //Then it should navigate to the log in view
    verify(editorialNavigator).navigateToLogIn();
  }

  @Test public void handleReactionsExceeded() {
    //Given an initialised presenter
    presenter.handleUserReaction();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should request to set said reaction
    when(editorialManager.setReaction("1", GROUP_ID, "laugh")).thenReturn(Single.just(
        new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.REACTIONS_EXCEEDED)));
    //The user chooses a reaction
    reactionClickEvent.onNext(new ReactionEvent("1", "laugh", GROUP_ID));
    verify(view).showLogInDialog();
  }

  @Test public void handleNetworkError() {
    //Given an initialised presenter
    presenter.handleUserReaction();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should request to set said reaction
    when(editorialManager.setReaction("1", GROUP_ID, "laugh")).thenReturn(Single.just(
        new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.NETWORK_ERROR)));
    //The user chooses a reaction
    reactionClickEvent.onNext(new ReactionEvent("1", "laugh", GROUP_ID));
    verify(view).showNetworkErrorToast();
  }

  @Test public void handleGeneralError() {
    //Given an initialised presenter
    presenter.handleUserReaction();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should request to set said reaction
    when(editorialManager.setReaction("1", GROUP_ID, "laugh")).thenReturn(Single.just(
        new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.GENERAL_ERROR)));
    //The user chooses a reaction
    reactionClickEvent.onNext(new ReactionEvent("1", "laugh", GROUP_ID));
    verify(view).showGenericErrorToast();
  }
}
