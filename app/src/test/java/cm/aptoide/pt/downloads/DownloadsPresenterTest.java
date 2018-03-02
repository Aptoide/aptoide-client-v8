package cm.aptoide.pt.downloads;

import cm.aptoide.pt.download.view.DownloadsFragment;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.DownloadsPresenter;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 2/28/18.
 */

public class DownloadsPresenterTest {

  @Mock private DownloadsFragment view;
  @Mock private InstallManager installManager;
  private DownloadsPresenter downloadsPresenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private ArrayList<Install> downloadsList;
  private ArrayList<Install> activeDownloadsList;
  private ArrayList<Install> standByDownloadsList;
  private ArrayList<Install> completedDownloadsList;

  @Before public void setupDownloadsPresenterTest() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();

    buildActiveDownloadsList();
    buildStandByDownloadsList();
    buildCompletedDownloadsList();

    downloadsPresenter = new DownloadsPresenter(view, installManager, Schedulers.immediate(),
        Schedulers.immediate());

    downloadsList = new ArrayList<>();
    downloadsList.addAll(activeDownloadsList);
    downloadsList.addAll(standByDownloadsList);
    downloadsList.addAll(completedDownloadsList);

    //simulate view lifecycle event
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void getActiveDownloads() {
    //Given an initialized presenter with a view and an installManager
    //When resume lifecycle event is called
    //And when getInstalations is called
    when(installManager.getInstallations()).thenReturn(Observable.just(activeDownloadsList));
    downloadsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //the active downloads should be shown in the UI.
    verify(installManager).getInstallations();
    verify(view).showActiveDownloads(activeDownloadsList);
    //and no other installs should be shown in the UI
    verify(view, never()).showCompletedDownloads(activeDownloadsList);
    verify(view, never()).showStandByDownloads(activeDownloadsList);
  }

  @Test public void getStandByDownloads() {
    //Given an initialized presenter with a view and an installManager
    //When resume lifecycle event is called
    //And when getInstalations is called
    when(installManager.getInstallations()).thenReturn(Observable.just(standByDownloadsList));
    downloadsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //the standby downloads should be shown in the UI.
    verify(installManager).getInstallations();
    verify(view).showStandByDownloads(standByDownloadsList);
    //and no other installs should be shown in the UI
    verify(view, never()).showCompletedDownloads(standByDownloadsList);
    verify(view, never()).showActiveDownloads(standByDownloadsList);
  }

  @Test public void getCompletedDownloads() {
    //Given an initialized presenter with a view and an installManager
    //When resume lifecycle event is called
    //And when getInstalations is called
    when(installManager.getInstallations()).thenReturn(Observable.just(completedDownloadsList));
    downloadsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //the completed downloads should be shown in the UI.
    verify(installManager).getInstallations();
    verify(view).showCompletedDownloads(completedDownloadsList);
    //and no other installs should be shown in the UI
    verify(view, never()).showActiveDownloads(completedDownloadsList);
    verify(view, never()).showStandByDownloads(completedDownloadsList);
  }

  @Test public void getDownloadsGeneral() {

    //Given an initialized presenter with a view and an installManager
    //When resume lifecycle event is called
    //And when getInstalations is called
    when(installManager.getInstallations()).thenReturn(Observable.just(downloadsList));
    downloadsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //The active, standby and completed downloads should be shown in the UI.
    verify(view).showActiveDownloads(activeDownloadsList);
    verify(view).showCompletedDownloads(completedDownloadsList);
    verify(view).showStandByDownloads(standByDownloadsList);
  }

  @Test public void getDownloadsWithEmptyListError() {
    //Given an initialized presenter with a view and an installManager
    //When resume lifecycle event is called
    //And when getInstalations is called
    when(installManager.getInstallations()).thenReturn(Observable.just(Collections.emptyList()));
    downloadsPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.RESUME);

    //then an empty downloads list should be shown in the UI
    verify(view).showEmptyDownloadList();
    //no other show methods should be called
    verify(view, never()).showActiveDownloads(Collections.emptyList());
    verify(view, never()).showCompletedDownloads(Collections.emptyList());
    verify(view, never()).showStandByDownloads(Collections.emptyList());
  }

  private void buildActiveDownloadsList() {
    activeDownloadsList = new ArrayList<>();
    activeDownloadsList.add(
        new Install(30, Install.InstallationStatus.INSTALLING, Install.InstallationType.UPDATE,
            true, -1, null, "com.whatsapp", 1221, "Whatsapp", ""));
  }

  private void buildStandByDownloadsList() {
    standByDownloadsList = new ArrayList<>();
    standByDownloadsList.add(
        new Install(20, Install.InstallationStatus.PAUSED, Install.InstallationType.INSTALL, false,
            -1, null, "com.facebook.katana", 48, "Facebook", ""));

    standByDownloadsList.add(
        new Install(20, Install.InstallationStatus.IN_QUEUE, Install.InstallationType.INSTALL,
            false, -1, null, "com.orca.facebook", 48, "Messenger", ""));
    standByDownloadsList.add(
        new Install(20, Install.InstallationStatus.GENERIC_ERROR, Install.InstallationType.INSTALL,
            false, -1, null, "org.telegram.messenger", 48, "Telegram", ""));
    standByDownloadsList.add(new Install(1906, Install.InstallationStatus.INSTALLATION_TIMEOUT,
        Install.InstallationType.INSTALL, false, -1, null, "com.ikeyboard.theme.sportingcp", 1906,
        "Sporting CP Keyboard Theme", ""));
    standByDownloadsList.add(new Install(218, Install.InstallationStatus.NOT_ENOUGH_SPACE_ERROR,
        Install.InstallationType.INSTALL, false, -1, null, "com.supercell.clashroyale", 218,
        "Clash Royale", ""));
  }

  private void buildCompletedDownloadsList() {
    completedDownloadsList = new ArrayList<>();
    completedDownloadsList.add(
        new Install(10, Install.InstallationStatus.INSTALLED, Install.InstallationType.UPDATE,
            false, -1, null, "cm.aptoide.pt", 123, "Aptoide", ""));
    completedDownloadsList.add(
        new Install(50, Install.InstallationStatus.UNINSTALLED, Install.InstallationType.INSTALL,
            false, -1, null, "com.instagram.android", 19302, "Instagramado", ""));
  }
}
