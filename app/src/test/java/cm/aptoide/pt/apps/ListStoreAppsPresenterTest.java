package cm.aptoide.pt.apps;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.ListStoreAppsFragment;
import cm.aptoide.pt.view.app.ListStoreAppsNavigator;
import cm.aptoide.pt.view.app.ListStoreAppsPresenter;
import java.util.ArrayList;
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

/**
 * Created by jdandrade on 22/02/2018.
 */

public class ListStoreAppsPresenterTest {

  private static final int STORE_ID_TEST = 15;
  private static final int LIMIT_APPS_TEST = 20;

  @Mock private AppCenter appCenter;

  @Mock private ListStoreAppsFragment view;

  @Mock private ListStoreAppsNavigator listStoreAppsNavigator;

  @Mock private CrashReport crashReporter;

  private ListStoreAppsPresenter listStoreAppsPresenter;

  private AppsList appsModel;
  private AppsList appsModelWithGenericError;
  private AppsList appsModelWithNetworkError;
  private AppsList appsModelLoading;

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setupListStoreAppsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    ArrayList<Application> apps = new ArrayList<>();
    apps.add(new Application("Aptoide", "", (float) 4.5, 1000, "cm.aptoide.com", 10, "", ""));
    apps.add(
        new Application("Fit2Gather", "", (float) 5, 100, "com.fijuro.fit2gather", 357, "", ""));
    appsModel = new AppsList(apps, false, LIMIT_APPS_TEST);
    appsModelWithGenericError = new AppsList(AppsList.Error.GENERIC);
    appsModelWithNetworkError = new AppsList(AppsList.Error.NETWORK);
    appsModelLoading = new AppsList(true);

    listStoreAppsPresenter =
        new ListStoreAppsPresenter(view, STORE_ID_TEST, Schedulers.immediate(), appCenter,
            crashReporter, listStoreAppsNavigator, LIMIT_APPS_TEST);

    //simulate view lifecycle event
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void getAppsFromRepositoryAndShowInView() {
    //Given an initialised ListStoreAppsPresenter with a STORE_ID and LIMIT of apps
    //When onCreate lifecycle call event happens
    //And apps are requested to the model
    when(appCenter.getApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(Single.just(appsModel));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then all LIMIT apps are shown into the UI
    verify(view).setApps(appsModel.getList());
  }

  @Test public void getAppsFromRepositoryWithGenericError() {
    //Given an initialised ListStoreAppsPresenter with a STORE_ID and LIMIT of apps
    //When onCreate lifecycle call event happens
    //And there is a generic error while requesting apps to the app model
    when(appCenter.getApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithGenericError));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then show generic error on UI
    verify(view).showGenericError();
  }

  @Test public void getAppsFromRepositoryWithNetworkError() {
    //Given an initialised ListStoreAppsPresenter with a STORE_ID and LIMIT of apps
    //When onCreate lifecycle call event happens
    //And there is a network error while requesting apps to the app model
    when(appCenter.getApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithNetworkError));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then show network error on UI
    verify(view).showNetworkError();
  }

  @Test public void getAppsFromRepositoryWhileItIsLoading() {
    //Given an initialised ListStoreAppsPresenter with a STORE_ID and LIMIT of apps
    //When onCreate lifecycle call event happens
    //And there app model is loading
    when(appCenter.getApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelLoading));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then should do nothing
    verify(view, never()).setApps(appsModelLoading.getList());
    verify(view, never()).showGenericError();
    verify(view, never()).showNetworkError();
  }

  @Test public void openAppViewFragmentOnAppClick() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens

    PublishSubject<Application> appClickEvent = PublishSubject.create();
    Application aptoide =
        new Application("Aptoide", "", (float) 4.5, 1000, "cm.aptoide.com", 10, "", "");

    when(view.getAppClick()).thenReturn(appClickEvent);

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //And an app is clicked
    appClickEvent.onNext(aptoide);

    //Then should navigate to app view of the selected app
    verify(listStoreAppsNavigator).navigateToAppView(aptoide.getAppId(), aptoide.getPackageName());
  }

  @Test public void loadAppsAfterReachingBottomWithSuccess() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view reaches the bottom
    PublishSubject<Object> reachedBottomEvent = PublishSubject.create();
    when(view.reachesBottom()).thenReturn(reachedBottomEvent);

    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(Single.just(appsModel));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    reachedBottomEvent.onNext(null);

    //then should show loading
    verify(view).showLoading();
    //and request for the next apps
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and add the new apps to the UI
    verify(view).addApps(appsModel.getList());
    //and hide the loading
    verify(view).hideLoading();
  }

  @Test public void loadAppsAfterReachingBottomWithGenericError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view reaches the bottom
    PublishSubject<Object> reachedBottomEvent = PublishSubject.create();
    when(view.reachesBottom()).thenReturn(reachedBottomEvent);

    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithGenericError));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    reachedBottomEvent.onNext(null);

    //then should show loading
    verify(view).showLoading();
    //and request the next apps
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and show generic error
    verify(view).showGenericError();
  }

  @Test public void loadAppsAfterReachingBottomWithNetworkError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view reaches the bottom
    PublishSubject<Object> reachedBottomEvent = PublishSubject.create();
    when(view.reachesBottom()).thenReturn(reachedBottomEvent);

    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithNetworkError));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    reachedBottomEvent.onNext(null);

    //then should show loading
    verify(view).showLoading();
    //and request for  the next apps
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and show network error
    verify(view).showNetworkError();
  }

  @Test public void loadAppsAfterReachingBottomWithLoading() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view reaches the bottom
    PublishSubject<Object> reachedBottomEvent = PublishSubject.create();
    when(view.reachesBottom()).thenReturn(reachedBottomEvent);

    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelLoading));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    reachedBottomEvent.onNext(null);

    //then should show loading
    verify(view).showLoading();
    //and request the next apps
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //hide loading
    verify(view).hideLoading();
    //and should not show new apps in the UI or errors
    verify(view, never()).addApps(appsModelLoading.getList());
    verify(view, never()).showGenericError();
    verify(view, never()).showNetworkError();
  }

  @Test public void loadAppsAfterRefreshWithSuccess() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view is refreshed by the user
    PublishSubject<Void> refreshEvent = PublishSubject.create();
    when(view.getRefreshEvent()).thenReturn(refreshEvent);

    when(appCenter.loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModel));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    refreshEvent.onNext(null);

    //Then when new apps are requested loadFreshApps
    verify(appCenter).loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and hide the refresh loading
    verify(view).hideRefreshLoading();
    //and show success - add apps list
    verify(view).setApps(appsModel.getList());
  }

  @Test public void loadAppsAfterRefreshWithGenericError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view is refreshed by the user
    PublishSubject<Void> refreshEvent = PublishSubject.create();
    when(view.getRefreshEvent()).thenReturn(refreshEvent);

    when(appCenter.loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithGenericError));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    refreshEvent.onNext(null);

    //Then when new apps are requested
    verify(appCenter).loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //hide refresh loading
    verify(view).hideRefreshLoading();
    // and show Generic error
    verify(view).showGenericError();
  }

  @Test public void loadAppsAfterRefreshWithNetworkError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view is refreshed by the user
    PublishSubject<Void> refreshEvent = PublishSubject.create();
    when(view.getRefreshEvent()).thenReturn(refreshEvent);

    when(appCenter.loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithNetworkError));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    refreshEvent.onNext(null);

    //Then when new apps are requested
    verify(appCenter).loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and hide refresh loading
    verify(view).hideRefreshLoading();
    //and error - Generic error- show Generic error
    verify(view).showNetworkError();
  }

  @Test public void loadAppsAfterRefreshWithLoadingError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the view is refreshed by the user
    //and the app model is processing another request
    PublishSubject<Void> refreshEvent = PublishSubject.create();
    when(view.getRefreshEvent()).thenReturn(refreshEvent);

    when(appCenter.loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelLoading));

    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    refreshEvent.onNext(null);

    //Then when new apps are requested
    verify(appCenter).loadFreshApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and apps model is loading
    //hide loading
    verify(view).hideRefreshLoading();
    //and do nothing else
    verify(view, never()).setApps(appsModelLoading.getList());
    verify(view, never()).showGenericError();
    verify(view, never()).showNetworkError();
  }

  @Test public void getAppsAfterRetryClickWithGenericError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the user is on the no network view
    //and clicks on the retry button

    PublishSubject<Void> retryClickEvent = PublishSubject.create();
    when(view.getRetryEvent()).thenReturn(retryClickEvent);
    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithGenericError));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    retryClickEvent.onNext(null);

    //then a loading should be shown in the UI
    verify(view).showStartingLoading();
    //and a request apps should be done
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and hide loading
    verify(view).hideLoading();
    //and generic error should be shown in the UI
    verify(view).showGenericError();
  }

  @Test public void getAppsAfterRetryClickWithNetworkError() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the user is on the no network view
    //and clicks on the retry button

    PublishSubject<Void> retryClickEvent = PublishSubject.create();
    when(view.getRetryEvent()).thenReturn(retryClickEvent);
    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelWithNetworkError));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    retryClickEvent.onNext(null);

    //then a loading should be shown in the UI
    verify(view).showStartingLoading();
    //and a request apps should be done
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and hide loading
    verify(view).hideLoading();
    //and network error should be shown in the UI
    verify(view).showNetworkError();
  }

  @Test public void getAppsAfterRetryClickWithLoading() {
    //Given an initialized ListStoreAppsPresenter with a STORE_ID and a limit of apps
    //When onCreate lifecycle call event happens
    //and the user is on the no network view
    //and clicks on the retry button

    PublishSubject<Void> retryClickEvent = PublishSubject.create();
    when(view.getRetryEvent()).thenReturn(retryClickEvent);
    when(appCenter.loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST)).thenReturn(
        Single.just(appsModelLoading));
    listStoreAppsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    retryClickEvent.onNext(null);

    //then a loading should be shown in the UI
    verify(view).showStartingLoading();
    //and a request apps should be done
    verify(appCenter).loadNextApps(STORE_ID_TEST, LIMIT_APPS_TEST);
    //and apps model is loading
    //hide loading
    verify(view).hideLoading();
    //should do nothing else
    verify(view, never()).showNetworkError();
    verify(view, never()).showGenericError();
    verify(view, never()).addApps(appsModelLoading.getList());
  }
}
