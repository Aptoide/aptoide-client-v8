package cm.aptoide.pt.navigation;

import android.content.SharedPreferences;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.install.AutoUpdate;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.MainPresenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.util.ApkFy;
import cm.aptoide.pt.view.DeepLinkManager;
import cm.aptoide.pt.view.MainActivity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class BottomNavigationPresenterTest {

  private static final int MENU_ITEM_ID_TEST = 2;
  @Mock private InstallManager installManager;
  @Mock private RootInstallationRetryHandler rootInstallationRetryHandler;
  @Mock private ApkFy apkFy;
  @Mock private AutoUpdate autoUpdate;
  @Mock private ContentPuller contentPuller;
  @Mock private NotificationSyncScheduler notificationSyncScheduler;
  @Mock private InstallCompletedNotifier installCompletedNotifier;
  @Mock private SharedPreferences sharedPreferences;
  @Mock private FragmentNavigator fragmentNavigator;
  @Mock private DeepLinkManager deepLinkManager;
  @Mock private BottomNavigationActivity bottomNavigationActivity;
  @Mock private MainActivity mainView;
  private MainPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Integer> navigationEvent;

  @Before public void setupBottomNavigationPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    navigationEvent = PublishSubject.create();

    presenter = new MainPresenter(mainView, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFy, autoUpdate, contentPuller, notificationSyncScheduler,
        installCompletedNotifier, sharedPreferences, sharedPreferences, fragmentNavigator,
        deepLinkManager, "", "", true, bottomNavigationActivity, Schedulers.immediate());

    //simulate view lifecycle event
    when(mainView.getLifecycle()).thenReturn(lifecycleEvent);
    when(bottomNavigationActivity.navigationEvent()).thenReturn(navigationEvent);
  }

  @Test public void onNavigationRequestedNavigateToView() {
    //Given an initialised MainPresenter
    presenter.present();
    //And Bottom navigation is visible to the user
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When the user clicks a menu item
    navigationEvent.onNext(MENU_ITEM_ID_TEST);
    //Then that menu item becomes focused
    //And the respective view is shown to the user
    verify(bottomNavigationActivity).showFragment(MENU_ITEM_ID_TEST);
  }
}
