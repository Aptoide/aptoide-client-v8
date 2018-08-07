package cm.aptoide.pt.app.view;

import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.AppCoinsInfoManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by tiagopedrinho on 06/08/18.
 */

public class AppCoinsInfoManagerTest {

  @Mock private InstallManager installManager;
  private AppCoinsInfoManager manager;

  @Before public void setupAppCoinsInfoPresenter() {
    MockitoAnnotations.initMocks(this);
    manager = new AppCoinsInfoManager(installManager);
  }

  @Test public void loadButtonStateTest() {
    //Given an initialized AppCoinsInfoManager
    manager.loadButtonState();
    //It should ask the install manager if the app is installed
    when(installManager.isInstalled(AppCoinsInfoFragment.APPCWALLETPACKAGENAME)).thenReturn(
        Observable.just(false));
    verify(installManager).isInstalled(AppCoinsInfoFragment.APPCWALLETPACKAGENAME);
    manager.loadButtonState()
        .map(isInstalled -> isInstalled)
        .test()
        .assertValue(false);
  }
}