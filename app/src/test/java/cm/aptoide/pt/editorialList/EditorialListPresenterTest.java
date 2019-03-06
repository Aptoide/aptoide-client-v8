package cm.aptoide.pt.editorialList;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.home.HomeEvent;
import cm.aptoide.pt.presenter.View;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditorialListPresenterTest {
  @Mock EditorialListFragment view;
  @Mock EditorialListManager editorialListManager;
  @Mock AptoideAccountManager accountManager;
  @Mock EditorialListNavigator editorialListNavigator;
  @Mock EditorialListAnalytics editorialListAnalytics;
  @Mock CrashReport crashReporter;
  @Mock Account account;

  private EditorialListPresenter presenter;
  private EditorialListViewModel successEditorialViewModel;
  private EditorialListViewModel loadingEditorialViewModel;
  private EditorialListViewModel genericErrorEditorialViewModel;

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Object> bottomReachedEvent;
  private PublishSubject<Void> retryClickedEvent;
  private PublishSubject<Account> accountStatusEvent;
  private PublishSubject<Void> imageClickEvent;
  private PublishSubject<EditorialHomeEvent> cardClickEvent;
  private EditorialListViewModel networkErrorEditorialViewModel;
  private PublishSubject<Void> refreshEvent;

  @Before public void setupHomePresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    bottomReachedEvent = PublishSubject.create();
    retryClickedEvent = PublishSubject.create();
    accountStatusEvent = PublishSubject.create();
    imageClickEvent = PublishSubject.create();
    cardClickEvent = PublishSubject.create();
    refreshEvent = PublishSubject.create();

    presenter = new EditorialListPresenter(view, editorialListManager, accountManager,
        editorialListNavigator, editorialListAnalytics, crashReporter, Schedulers.immediate());
    CurationCard curationCard = new CurationCard("1", "sub", "icon", "title");
    List<CurationCard> curationCardList = Collections.singletonList(curationCard);
    successEditorialViewModel = new EditorialListViewModel(curationCardList, 0, 0);
    loadingEditorialViewModel = new EditorialListViewModel(true);
    genericErrorEditorialViewModel =
        new EditorialListViewModel(EditorialListViewModel.Error.GENERIC);
    networkErrorEditorialViewModel =
        new EditorialListViewModel(EditorialListViewModel.Error.NETWORK);

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.reachesBottom()).thenReturn(bottomReachedEvent);
    when(view.retryClicked()).thenReturn(retryClickedEvent);
    when(view.editorialCardClicked()).thenReturn(cardClickEvent);
    when(accountManager.accountStatus()).thenReturn(accountStatusEvent);
    when(view.imageClick()).thenReturn(imageClickEvent);
    when(view.refreshes()).thenReturn(refreshEvent);
  }

  @Test public void onCreateLoadSuccessViewModelTest() {
    //When the viewModel is requested then it should return a viewModel
    when(editorialListManager.loadEditorialListViewModel(false, false)).thenReturn(
        Single.just(successEditorialViewModel));
    //Given an initialized Presenter
    presenter.onCreateLoadViewModel();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should display loading
    verify(view).showLoading();
    //After a success viewModel it should hide the loading
    verify(view).hideLoading();
    //And populate the view with the cards from the viewModel
    verify(view).populateView(successEditorialViewModel);
    //And hide the loadMore card if there's one
    verify(view).hideLoadMore();
  }

  @Test public void onCreateLoadLoadingViewModelTest() {
    //When the viewModel is requested then it should return a viewModel
    when(editorialListManager.loadEditorialListViewModel(false, false)).thenReturn(
        Single.just(loadingEditorialViewModel));
    //Given an initialized Presenter
    presenter.onCreateLoadViewModel();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should display loading
    verify(view).showLoading();
    //After a loading viewModel it should not hide the loading
    verify(view, never()).hideLoading();
    //And hide the loadMore card if there's one
    verify(view).hideLoadMore();
  }

  @Test public void onCreateLoadNetworkErrorViewModelTest() {
    //When the viewModel is requested then it should return a viewModel
    when(editorialListManager.loadEditorialListViewModel(false, false)).thenReturn(
        Single.just(networkErrorEditorialViewModel));
    //Given an initialized Presenter
    presenter.onCreateLoadViewModel();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should display loading
    verify(view).showLoading();
    //After an error viewModel it should hide the loading
    verify(view).hideLoading();
    //And show an error view
    verify(view).showNetworkError();
    //And shouldn't populate the view
    verify(view, never()).populateView(networkErrorEditorialViewModel);
    //And hide the loadMore card if there's one
    verify(view).hideLoadMore();
  }

  @Test public void onCreateLoadGenericErrorViewModelTest() {
    //When the viewModel is requested then it should return a viewModel
    when(editorialListManager.loadEditorialListViewModel(false, false)).thenReturn(
        Single.just(genericErrorEditorialViewModel));
    //Given an initialized Presenter
    presenter.onCreateLoadViewModel();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //It should display loading
    verify(view).showLoading();
    //After an error viewModel it should hide the loading
    verify(view).hideLoading();
    //And show an error view
    verify(view).showGenericError();
    //And shouldn't populate the view
    verify(view, never()).populateView(genericErrorEditorialViewModel);
    //And hide the loadMore card if there's one
    verify(view).hideLoadMore();
  }

  @Test public void handleEditorialCardClickTest() {
    //Given an initialized Presenter
    presenter.handleEditorialCardClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When there's is a click on an Editorial Card, it should emit an EditorialHomeEvent
    cardClickEvent.onNext(new EditorialHomeEvent("1", null, -1, HomeEvent.Type.EDITORIAL));
    //Then it should send editorial interact analytic event
    verify(editorialListAnalytics).sendEditorialInteractEvent("1");
    //And navigate to the specified editorial view
    verify(editorialListNavigator).navigateToEditorial("1");
  }

  @Test public void handleRetryClickTest() {
    //Given an initialised presenter
    presenter.handleRetryClick();
    when(editorialListManager.loadEditorialListViewModel(false, true)).thenReturn(
        Single.just(successEditorialViewModel));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    retryClickedEvent.onNext(null);
    //Then the editorial cards should be shown
    verify(view).update(successEditorialViewModel.getCurationCards());
    //Then it should hide the loading indicator
    verify(view).hideLoading();
    //Then it should hide the load more indicator (if exists)
    verify(view).hideLoadMore();
  }

  @Test public void handlePullToRefreshTest() {
    //Given an initialised presenter
    presenter.handlePullToRefresh();
    when(editorialListManager.loadEditorialListViewModel(false, true)).thenReturn(
        Single.just(successEditorialViewModel));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    refreshEvent.onNext(null);
    //Then it should hide the swipe refresh icon
    verify(view).hideRefresh();
    //Then the editorial cards should be shown
    verify(view).update(successEditorialViewModel.getCurationCards());
    //Then it should hide the loading indicator
    verify(view).hideLoading();
    //Then it should hide the load more indicator (if exists)
    verify(view).hideLoadMore();
  }

  @Test public void handleBottomReachedTest() {
    //Given an initialised presenter
    presenter.handleBottomReached();
    when(editorialListManager.loadEditorialListViewModel(true, false)).thenReturn(
        Single.just(successEditorialViewModel));
    when(editorialListManager.hasMore()).thenReturn(true);
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When scrolling to the end of the view is reached
    //And there are more cards available to load
    bottomReachedEvent.onNext(new Object());
    //Then it should show the load more progress indicator
    verify(view).showLoadMore();
    //Then it should request the next cards to the model repository
    verify(editorialListManager).loadEditorialListViewModel(true, false);
    //Then it should hide the load more progress indicator
    verify(view).hideLoadMore();
    verify(view).hideLoading();
    //Then it should show the view again with old cards and added cards, retaining list position
    verify(view).populateView(successEditorialViewModel);
  }

  @Test public void loadLoggedInUserImageUserTest() {
    //When the user is logged in
    when(account.getAvatar()).thenReturn("A string");
    when(account.isLoggedIn()).thenReturn(true);
    //Given an initialised presenter
    presenter.loadUserImage();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).setUserImage("A string");
    verify(view).showAvatar();
  }

  @Test public void loadNotLoggedInUserImageUserTest() {
    //When the user is logged in
    when(account.isLoggedIn()).thenReturn(false);
    //Given an initialised presenter
    presenter.loadUserImage();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).showAvatar();
  }

  @Test public void handleUserImageClickTest() {
    //Given an initialised presenter
    presenter.handleUserImageClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an user clicks the profile image
    imageClickEvent.onNext(null);
    //Then it should navigate to the Settings Fragment
    verify(editorialListNavigator).navigateToMyAccount();
  }
}
