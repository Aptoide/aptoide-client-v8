package cm.aptoide.pt.navigation;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.AppsFragment;
import cm.aptoide.pt.home.AppsManager;
import cm.aptoide.pt.home.AppsPresenter;
import cm.aptoide.pt.home.InstalledApp;
import cm.aptoide.pt.home.UpdateApp;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsPresenterTest {

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  @Mock private AppsFragment view;
  private AppsPresenter appsPresenter;
  private List<UpdateApp> updatesList;
  private List<InstalledApp> installedList;
  @Mock private AppsManager appsManager;

  @Before public void setupAppsPresenter() {
    MockitoAnnotations.initMocks(this);

    buildUpdatesList();
    buildInstallList();

    appsPresenter =
        new AppsPresenter(view, appsManager, Schedulers.immediate(), Schedulers.immediate(),
            CrashReport.getInstance());
    //simulate view lifecycle event
    lifecycleEvent = PublishSubject.create();
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void getUpdatesTest() {
    //Given an initialized presenter with view and installManager
    //When a CREATE event is called
    //and get updates list is also called
    when(appsManager.getUpdatesList()).thenReturn(Observable.just(updatesList));
    appsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then all the update apps should be shown
    verify(view).showUpdatesList(updatesList);
  }

  @Test public void getInstalledAppsListTest() {
    //Given an initialized presenter with view and installManager
    //When a CREATE event is called
    //and get Installations is called
    when(appsManager.getInstalledApps()).thenReturn(Observable.just(installedList));
    appsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then all the installed apps should be shown
    verify(view).showInstalledApps(installedList);
  }

  private void buildUpdatesList() {
    updatesList = new ArrayList<>();
    updatesList.add(new UpdateApp("Aptoide", "cm.aptoide.pt", "123.33.0", "icon_path"));
    updatesList.add(
        new UpdateApp("Clash Royale", "com.supercell.clashroyale", "9123", "icon_path"));
  }

  private void buildInstallList() {
    installedList = new ArrayList<>();
    installedList.add(new InstalledApp("Twitter", "com.twitter.android", "7.37.0", "icon_path"));
    installedList.add(new InstalledApp("WhatsApp", "com.whatsapp", "2.18.74", "icon_path"));
  }
}
