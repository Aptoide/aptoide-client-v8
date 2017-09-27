/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.install.AutoUpdate;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.presenter.MainPresenter;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.util.ApkFy;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigatorActivity;
import com.jakewharton.rxrelay.PublishRelay;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 06-05-2016.
 */
public class MainActivity extends TabNavigatorActivity
    implements MainView, DeepLinkManager.DeepLinkMessages {

  private static final int LAYOUT = R.layout.frame_layout;

  private static final String TAG = MainActivity.class.getSimpleName();
  private InstallManager installManager;
  private View snackBarLayout;
  private PublishRelay<Void> installErrorsDismissEvent;
  private Snackbar snackbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LAYOUT);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    installManager = application.getInstallManager(InstallerFactory.DEFAULT);
    final AptoideAccountManager accountManager = application.getAccountManager();
    final String marketName = application.getMarketName();
    final AutoUpdate autoUpdate =
        new AutoUpdate(this, new DownloadFactory(marketName), new PermissionManager(),
            installManager, getResources(), application.getAutoUpdateUrl(), R.mipmap.ic_launcher,
            false, marketName);
    final OkHttpClient httpClient = application.getDefaultClient();

    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final SharedPreferences sharedPreferences = application.getDefaultSharedPreferences();
    final SharedPreferences securePreferences =
        SecurePreferencesImplementation.getInstance(getApplicationContext(), sharedPreferences);

    final StoreRepository storeRepository =
        RepositoryFactory.getStoreRepository(getApplicationContext());

    final FragmentNavigator fragmentNavigator = getFragmentNavigator();

    final StoreUtilsProxy storeUtilsProxy =
        new StoreUtilsProxy(accountManager, application.getAccountSettingsBodyInterceptorPoolV7(),
            new StoreCredentialsProviderImpl(
                AccessorFactory.getAccessorFor(application.getDatabase(), Store.class)),
            AccessorFactory.getAccessorFor(application.getDatabase(), Store.class), httpClient,
            converterFactory, application.getTokenInvalidator(), sharedPreferences);

    final String defaultTheme = application.getDefaultThemeName();
    final DeepLinkManager deepLinkManager =
        new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator, this, this,
            sharedPreferences,
            AccessorFactory.getAccessorFor(application.getDatabase(), Store.class), defaultTheme,
            application.getDefaultStoreName(), application.getAptoideNavigationTracker(),
            application.getPageViewsAnalytics());

    final ApkFy apkFy = new ApkFy(this, getIntent(), securePreferences);

    final NotificationSyncScheduler notificationSyncScheduler =
        application.getNotificationSyncScheduler();

    InstallCompletedNotifier installCompletedNotifier =
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance());

    snackBarLayout = findViewById(R.id.snackbar_layout);
    installErrorsDismissEvent = PublishRelay.create();
    attachPresenter(
        new MainPresenter(this, installManager, application.getRootInstallationRetryHandler(),
            CrashReport.getInstance(), apkFy, autoUpdate, new ContentPuller(this),
            notificationSyncScheduler, installCompletedNotifier, sharedPreferences,
            securePreferences, fragmentNavigator, deepLinkManager,
            application.getDefaultStoreName(), defaultTheme), savedInstanceState);
  }

  @Override public void showInstallationError(int numberOfErrors) {
    String title;
    if (numberOfErrors == 1) {
      title = getString(R.string.generalscreen_short_root_install_single_app_timeout_error_message);
    } else {
      title = getString(R.string.generalscreen_short_root_install_timeout_error_message,
          numberOfErrors);
    }

    Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
      @Override public void onDismissed(Snackbar snackbar, int event) {
        super.onDismissed(snackbar, event);
        if (event == DISMISS_EVENT_SWIPE) {
          installErrorsDismissEvent.call(null);
        }
      }
    };
    snackbar = Snackbar.make(snackBarLayout, title, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.generalscreen_short_root_install_timeout_error_action,
            view -> installManager.retryTimedOutInstallations()
                .andThen(installManager.cleanTimedOutInstalls())
                .subscribe())
        .addCallback(snackbarCallback);
    snackbar.show();
  }

  @Override public void dismissInstallationError() {
    if (snackbar != null) {
      snackbar.dismiss();
    }
  }

  @Override public void showInstallationSuccessMessage() {
    ShowMessage.asSnack(snackBarLayout, R.string.generalscreen_short_root_install_success_install);
  }

  @Override public Observable<Void> getInstallErrorsDismiss() {
    return installErrorsDismissEvent;
  }

  @Override public Intent getIntentAfterCreate() {
    return getIntent();
  }

  @Override public void showStoreAlreadyAdded() {
    ShowMessage.asLongSnack(this, getString(R.string.store_already_added));
  }

  @Override public void showStoreFollowed(String storeName) {
    ShowMessage.asLongSnack(this,
        AptoideUtils.StringU.getFormattedString(R.string.store_followed, getResources(),
            storeName));
  }
}
