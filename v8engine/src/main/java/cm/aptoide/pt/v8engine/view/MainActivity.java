/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.AutoUpdate;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
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
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 06-05-2016.
 */
public class MainActivity extends TabNavigatorActivity
    implements MainView, DeepLinkManager.DeepLinkMessages {

  private static final int LAYOUT = R.layout.frame_layout;

  @Partners @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LAYOUT);

    final AptoideAccountManager accountManager =
        ((V8Engine) getApplicationContext()).getAccountManager();

    final InstallManager installManager =
        ((V8Engine) getApplicationContext()).getInstallManager(InstallerFactory.DEFAULT);

    final AutoUpdate autoUpdate =
        new AutoUpdate(this, new DownloadFactory(), new PermissionManager(), installManager);

    final OkHttpClient httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();

    final Converter.Factory converterFactory = WebService.getDefaultConverter();

    final StoreRepository storeRepository = RepositoryFactory.getStoreRepository();

    final FragmentNavigator fragmentNavigator = getFragmentNavigator();

    final StoreUtilsProxy storeUtilsProxy = new StoreUtilsProxy(accountManager,
        ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV7(),
        new StoreCredentialsProviderImpl(), AccessorFactory.getAccessorFor(Store.class), httpClient,
        converterFactory);

    final DeepLinkManager deepLinkManager =
        new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator, this, this);

    final ApkFy apkFy = new ApkFy(this, getIntent());

    final NotificationSyncScheduler notificationSyncScheduler =
        ((V8Engine) getApplicationContext()).getNotificationSyncScheduler();

    attachPresenter(new MainPresenter(this, apkFy, autoUpdate, new ContentPuller(this),
            notificationSyncScheduler, CrashReport.getInstance(), fragmentNavigator, deepLinkManager),
        savedInstanceState);
  }

  @Override public Intent getIntentAfterCreate() {
    return getIntent();
  }

  @Override public void showStoreAlreadyAdded() {
    ShowMessage.asLongSnack(this, getString(R.string.store_already_added));
  }

  @Override public void showStoreFollowed(String storeName) {
    ShowMessage.asLongSnack(this,
        AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
  }
}
