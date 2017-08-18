/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.AutoUpdate;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.install.InstallCompletedNotifier;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.notification.ContentPuller;
import cm.aptoide.pt.v8engine.notification.NotificationSyncScheduler;
import cm.aptoide.pt.v8engine.presenter.MainPresenter;
import cm.aptoide.pt.v8engine.presenter.MainView;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.util.ApkFy;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigatorActivity;
import com.appsee.Appsee;
import com.appsee.AppseeListener;
import com.appsee.AppseeScreenDetectedInfo;
import com.appsee.AppseeSessionEndedInfo;
import com.appsee.AppseeSessionEndingInfo;
import com.appsee.AppseeSessionStartedInfo;
import com.appsee.AppseeSessionStartingInfo;
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

  @Partners @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LAYOUT);

    //AppSee initialization
    Appsee.start(getString(R.string.APPSEE_API_KEY));
    Appsee.setUserId(((V8Engine) getApplicationContext()).getIdsRepository()
        .getUniqueIdentifier());

    // Set your AppDelegate as the Appsee delegate
    Appsee.addAppseeListener(new AppseeListener() {

      @Override
      public void onAppseeSessionStarting(AppseeSessionStartingInfo appseeSessionStartingInfo) {

      }

      @Override
      public void onAppseeSessionStarted(AppseeSessionStartedInfo appseeSessionStartedInfo) {

      }

      @Override public void onAppseeSessionEnding(AppseeSessionEndingInfo appseeSessionEndingInfo) {

      }

      @Override public void onAppseeSessionEnded(AppseeSessionEndedInfo appseeSessionEndedInfo) {

      }

      @Override
      public void onAppseeScreenDetected(AppseeScreenDetectedInfo appseeScreenDetectedInfo) {

        if (appseeScreenDetectedInfo != null && !TextUtils.isEmpty(
            appseeScreenDetectedInfo.getScreenName())) {

          if ("MainActivity".equals(appseeScreenDetectedInfo.getScreenName())) {
            // To ignore a new screen
            appseeScreenDetectedInfo.setScreenName(null);
          }
        }

        // You can also change the screen's name
        //appseeScreenDetectedInfo.setScreenName("MyScreen"));
      }
    });

    installManager =
        ((V8Engine) getApplicationContext()).getInstallManager(InstallerFactory.DEFAULT);
    final AptoideAccountManager accountManager =
        ((V8Engine) getApplicationContext()).getAccountManager();
    final AutoUpdate autoUpdate =
        new AutoUpdate(this, new DownloadFactory(), new PermissionManager(), installManager,
            getResources(), Application.getConfiguration()
            .getAutoUpdateUrl());
    final OkHttpClient httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();

    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final SharedPreferences sharedPreferences =
        ((V8Engine) getApplicationContext()).getDefaultSharedPreferences();
    final SharedPreferences securePreferences =
        SecurePreferencesImplementation.getInstance(getApplicationContext(), sharedPreferences);

    final StoreRepository storeRepository =
        RepositoryFactory.getStoreRepository(getApplicationContext());

    final FragmentNavigator fragmentNavigator = getFragmentNavigator();

    final StoreUtilsProxy storeUtilsProxy = new StoreUtilsProxy(accountManager,
        ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV7Pool(),
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
            ((V8Engine) getApplicationContext().getApplicationContext()).getDatabase(),
            Store.class)), AccessorFactory.getAccessorFor(
        ((V8Engine) getApplicationContext().getApplicationContext()).getDatabase(), Store.class),
        httpClient, converterFactory, ((V8Engine) getApplicationContext()).getTokenInvalidator(),
        sharedPreferences);

    final DeepLinkManager deepLinkManager =
        new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator, this, this,
            sharedPreferences, AccessorFactory.getAccessorFor(
            ((V8Engine) getApplicationContext().getApplicationContext()).getDatabase(),
            Store.class));

    final ApkFy apkFy = new ApkFy(this, getIntent(), securePreferences);

    final NotificationSyncScheduler notificationSyncScheduler =
        ((V8Engine) getApplicationContext()).getNotificationSyncScheduler();

    InstallCompletedNotifier installCompletedNotifier =
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance());

    snackBarLayout = findViewById(R.id.snackbar_layout);
    installErrorsDismissEvent = PublishRelay.create();
    attachPresenter(new MainPresenter(this, installManager,
        ((V8Engine) getApplicationContext()).getRootInstallationRetryHandler(),
        CrashReport.getInstance(), apkFy, autoUpdate, new ContentPuller(this),
        notificationSyncScheduler, installCompletedNotifier, sharedPreferences, securePreferences,
        fragmentNavigator, deepLinkManager), savedInstanceState);
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