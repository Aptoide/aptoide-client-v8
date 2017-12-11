package cm.aptoide.pt.ads;

import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.install.PackageRepository;
import rx.Single;

public class PackageRepositoryVersionCodeProvider implements AdsApplicationVersionCodeProvider {

  private final PackageRepository packageRepository;
  private final String packageName;

  public PackageRepositoryVersionCodeProvider(PackageRepository packageRepository,
      String packageName) {
    this.packageName = packageName;
    this.packageRepository = packageRepository;
  }

  @Override public Single<Integer> getApplicationVersionCode() {
    return packageRepository.getPackageVersionCode(packageName)
        .onErrorReturn(throwable -> -1);
  }
}
