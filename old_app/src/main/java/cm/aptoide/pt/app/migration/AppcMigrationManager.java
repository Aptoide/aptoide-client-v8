package cm.aptoide.pt.app.migration;

import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import rx.Observable;

public class AppcMigrationManager {

  private static final long BDS_STORE_ID = 1966380;

  private final AptoideInstalledAppsRepository repository;
  private final AppcMigrationRepository appcMigrationRepository;

  public AppcMigrationManager(AptoideInstalledAppsRepository repository,
      AppcMigrationRepository appcMigrationRepository) {
    this.repository = repository;
    this.appcMigrationRepository = appcMigrationRepository;
  }

  public Observable<Boolean> isMigrationApp(String packageName, String signature, int versionCode,
      long storeId, boolean hasAppc) {
    return repository.getInstalled(packageName)
        .map(installed -> installed != null
            && signature != null
            && installed.getSignature() != null
            && !signature.equals(installed.getSignature())
            && installed.getVersionCode() <= versionCode
            && storeId == BDS_STORE_ID
            && hasAppc);
  }

  public void addMigrationCandidate(String packageName) {
    appcMigrationRepository.addMigrationCandidate(packageName);
  }

  public void persistCandidate(String packageName) {
    appcMigrationRepository.persistCandidate(packageName);
  }

  public Observable<Boolean> isAppMigrated(String packageName) {
    return appcMigrationRepository.isAppMigrated(packageName);
  }
}
