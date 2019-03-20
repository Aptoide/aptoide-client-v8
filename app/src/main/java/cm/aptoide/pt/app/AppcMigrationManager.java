package cm.aptoide.pt.app;

import cm.aptoide.pt.install.InstalledRepository;
import rx.Observable;

public class AppcMigrationManager {

  private static final long BDS_STORE_ID = 1966380;

  private InstalledRepository repository;

  public AppcMigrationManager(InstalledRepository repository) {
    this.repository = repository;
  }

  public Observable<Boolean> isMigrationApp(String packageName, String signature, int versionCode,
      long storeId, boolean hasAppc) {
    return repository.getInstalled(packageName)
        .map(installed -> installed != null
            && !installed.getSignature()
            .equals(signature)
            && installed.getVersionCode() <= versionCode
            && storeId == BDS_STORE_ID && hasAppc);
  }
}
