/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.ads.MinimalAdMapper;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.download.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.install.rollback.RollbackFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallerFactory {

  public static final int DEFAULT = 0;
  public static final int ROLLBACK = 1;
  private final MinimalAdMapper adMapper;
  private final InstallerAnalytics installerAnalytics;

  public InstallerFactory(MinimalAdMapper adMapper, InstallerAnalytics installerAnalytics) {
    this.adMapper = adMapper;
    this.installerAnalytics = installerAnalytics;
  }

  public Installer create(Context context, int type) {
    switch (type) {
      case DEFAULT:
        return getDefaultInstaller(context);
      case ROLLBACK:
        return getRollbackInstaller(context);
      default:
        throw new IllegalArgumentException("Installer not supported: " + type);
    }
  }

  @NonNull private DefaultInstaller getDefaultInstaller(Context context) {
    return new DefaultInstaller(context.getPackageManager(),
        getInstallationProvider(((V8Engine) context.getApplicationContext()).getDownloadManager(),
            context.getApplicationContext()), new FileUtils(), Analytics.getInstance(),
        ToolboxManager.isDebug(
            ((V8Engine) context.getApplicationContext()).getDefaultSharedPreferences())
            || BuildConfig.DEBUG,
        RepositoryFactory.getInstalledRepository(context.getApplicationContext()), 180000,
        ((V8Engine) context.getApplicationContext()).getRootAvailabilityManager(),
        ((V8Engine) context.getApplicationContext()).getDefaultSharedPreferences(),
        installerAnalytics);
  }

  @NonNull private RollbackInstaller getRollbackInstaller(Context context) {
    return new RollbackInstaller(getDefaultInstaller(context),
        RepositoryFactory.getRollbackRepository(context.getApplicationContext()),
        new RollbackFactory(),
        getInstallationProvider(((V8Engine) context.getApplicationContext()).getDownloadManager(),
            context.getApplicationContext()));
  }

  @NonNull private DownloadInstallationProvider getInstallationProvider(
      AptoideDownloadManager downloadManager, Context context) {
    return new DownloadInstallationProvider(downloadManager,
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Download.class), RepositoryFactory.getInstalledRepository(context), adMapper,
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), StoredMinimalAd.class));
  }
}
