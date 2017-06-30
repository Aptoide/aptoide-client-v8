/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.ads.MinimalAdMapper;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.install.rollback.RollbackFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import com.crashlytics.android.answers.Answers;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallerFactory {

  public static final int DEFAULT = 0;
  public static final int ROLLBACK = 1;
  private final MinimalAdMapper adMapper;
  private Answers fabric;
  private Analytics analytics;

  public InstallerFactory(MinimalAdMapper adMapper, Answers fabric, Analytics analytics) {
    this.adMapper = adMapper;
    this.fabric = fabric;
    this.analytics = analytics;
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
        getInstallationProvider(((V8Engine) context.getApplicationContext()).getDownloadManager()),
        new FileUtils(), Analytics.getInstance(), ToolboxManager.isDebug(
        ((V8Engine) context.getApplicationContext()).getDefaultSharedPreferences())
        || BuildConfig.DEBUG, RepositoryFactory.getInstalledRepository(), 180000,
        ((V8Engine) context.getApplicationContext()).getRootAvailabilityManager(),
        ((V8Engine) context.getApplicationContext()).getDefaultSharedPreferences(),
        new InstallFabricEvents(analytics, fabric));
  }

  @NonNull private RollbackInstaller getRollbackInstaller(Context context) {
    return new RollbackInstaller(getDefaultInstaller(context),
        RepositoryFactory.getRollbackRepository(), new RollbackFactory(),
        getInstallationProvider(((V8Engine) context.getApplicationContext()).getDownloadManager()));
  }

  @NonNull private DownloadInstallationProvider getInstallationProvider(
      AptoideDownloadManager downloadManager) {
    return new DownloadInstallationProvider(downloadManager,
        AccessorFactory.getAccessorFor(Download.class), RepositoryFactory.getInstalledRepository(),
        adMapper);
  }
}
