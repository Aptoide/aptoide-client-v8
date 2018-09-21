package cm.aptoide.pt.app.view;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.AppCoinsInfoNavigator;
import cm.aptoide.pt.view.AppCoinsInfoPresenter;
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
 * Created by tiagopedrinho on 03/08/18.
 */

public class AppCoinsInfoPresenterTest {

  @Mock private AppCoinsInfoFragment view;
  @Mock private AppCoinsInfoNavigator navigator;
  @Mock private CrashReport crashReporter;
  @Mock private InstallManager installManager;

  private String packageName = "packageName";
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private AppCoinsInfoPresenter presenter;
  private PublishSubject<Void> coinbaseClickEvent;
  private PublishSubject<Void> walletClickEvent;
  private PublishSubject<Void> installClickEvent;

  @Before public void setupAppCoinsInfoPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    coinbaseClickEvent = PublishSubject.create();
    installClickEvent = PublishSubject.create();
    walletClickEvent = PublishSubject.create();

    presenter =
        new AppCoinsInfoPresenter(view, navigator, installManager, crashReporter, packageName,
            Schedulers.immediate());

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.installButtonClick()).thenReturn(installClickEvent);
    when(view.coinbaseLinkClick()).thenReturn(coinbaseClickEvent);
    when(view.appCoinsWalletLinkClick()).thenReturn(walletClickEvent);
  }

  @Test public void handleClickOnInstallButtonAppviewTest() {

    //Given an initialized AppcoinsInfoPresenter
    presenter.handleClickOnInstallButton();
    //And the wallet is not installed
    when(installManager.isInstalled(packageName)).thenReturn(Observable.just(false));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    installClickEvent.onNext(null);
    //Then it should navigate to the wallet AppView
    verify(navigator).navigateToAppCoinsWallet();
  }

  @Test public void handleClickOnInstallButtonOpenAppTest() {
    //Given an initialized AppCoinsInfoPresenter
    presenter.handleClickOnInstallButton();
    //And the wallet is installed
    when(installManager.isInstalled(packageName)).thenReturn(Observable.just(true));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    installClickEvent.onNext(null);
    //Then it should open the wallet
    verify(view).openApp(packageName);
  }

  @Test public void handleClickOnCoinbaseLinkTest() {
    //Given an initialized AppCoinsInfoPresenter
    presenter.handleClickOnCoinbaseLink();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And the user clicks on the link
    coinbaseClickEvent.onNext(null);
    //Then it should navigate to that link
    verify(navigator).navigateToCoinbaseLink();
  }

  @Test public void handleClickOnAppcWalletLinkTest() {
    //Given an initialized AppCoinsInfoPresenter
    presenter.handleClickOnAppcWalletLink();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And the user clicks on the link
    walletClickEvent.onNext(null);
    //Then it should navigate to the wallet AppView
    verify(navigator).navigateToAppCoinsWallet();
  }

  @Test public void handleClickOnButtonTextTest() {
    //Given an initialized AppCoinsInfoPresenter
    presenter.handleButtonText();
    //And the app is installed
    when(installManager.isInstalled(packageName)).thenReturn(Observable.just(true));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the button should show that the app is installed
    verify(view).setButtonText(true);
  }
}

