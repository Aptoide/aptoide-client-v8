/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.v8engine.install.installer.BackgroundInstaller;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.provider.RollbackFactory;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallerFactory {

  public static final int DEFAULT = 0;
  public static final int ROLLBACK = 1;
  public static final int BACKGROUND_ROLLBACK = 2;

  public Installer create(Context context, int type) {
    switch (type) {
      case DEFAULT:
        return getDefaultInstaller(context);
      case ROLLBACK:
        return getRollbackInstaller(context);
      case BACKGROUND_ROLLBACK:
        final BackgroundInstaller backgroundInstaller =
            new BackgroundInstaller(getInstallationProvider(), context,
                getRollbackInstaller(context));
        backgroundInstaller.startBackgroundService();
        return backgroundInstaller;
      default:
        throw new IllegalArgumentException("Installer not supported: " + type);
    }
  }

  @NonNull protected RollbackInstaller getRollbackInstaller(Context context) {
    return new RollbackInstaller(getInstallationProvider(), getDefaultInstaller(context),
        new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class)),
        new RollbackFactory());
  }

  @NonNull private DownloadInstallationProvider getInstallationProvider() {
    return new DownloadInstallationProvider(
        new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager()));
  }

  @NonNull private DefaultInstaller getDefaultInstaller(Context context) {
    return new DefaultInstaller(context.getPackageManager(), getInstallationProvider());
  }
}
