package cm.aptoide.pt.app.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.AppCoinsInfoNavigator;
import cm.aptoide.pt.view.AppCoinsInfoPresenter;
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

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private AppCoinsInfoPresenter presenter;
  private PublishSubject<Void> coinbaseClickEvent;
  private PublishSubject<Void> installClickEvent;

  @Before public void setupAppCoinsInfoPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    coinbaseClickEvent = PublishSubject.create();
    installClickEvent = PublishSubject.create();

    presenter = new AppCoinsInfoPresenter(view, navigator, crashReporter);

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
    when(view.installButtonClick()).thenReturn(installClickEvent);
    when(view.coinbaseLinkClick()).thenReturn(coinbaseClickEvent);
  }

  @Test public void handleClickOnInstallButtonTest() {
    //
    presenter.handleClickOnInstallButton();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    installClickEvent.onNext(null);
    verify(navigator).navigateToAppCoinsBDSWallet();
  }

  @Test public void handleClickOnCoinbaseLink(){
    presenter.handleClickOnCoinbaseLink();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    coinbaseClickEvent.onNext(null);
    verify(navigator).navigateToCoinbaseLink();
  }
}

