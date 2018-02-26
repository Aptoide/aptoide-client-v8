package cm.aptoide.pt.apps;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.ListStoreAppsFragment;
import cm.aptoide.pt.view.app.ListStoreAppsPresenter;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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

  @Mock private CrashReport crashReporter;

  @Mock private FragmentNavigator fragmentNavigator;

  private ListStoreAppsPresenter listStoreAppsPresenter;
  private AppsList appsModel;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setupListStoreAppsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    ArrayList<Application> apps = new ArrayList<>();
    apps.add(new Application("Aptoide", "", (float) 4.5, 1000, "cm.aptoide.com", 10));
    apps.add(new Application("Fit2Gather", "", (float) 5, 100, "com.fijuro.fit2gather", 357));
    appsModel = new AppsList(apps, false, LIMIT_APPS_TEST);

    listStoreAppsPresenter =
        new ListStoreAppsPresenter(view, STORE_ID_TEST, Schedulers.immediate(), appCenter,
            crashReporter, fragmentNavigator, LIMIT_APPS_TEST);

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
}
