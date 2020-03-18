package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.install.InstalledRepository
import rx.Observable

class AptoideInstallManager(val installedRepository: InstalledRepository,
                            val aptoideInstallService: AptoideInstallService) {

  fun addAptoideInstallCandidate(packageName: String) {
    aptoideInstallService.addAptoideInstallCandidate(packageName)
  }

  fun persistCandidate(packageName: String) {
    aptoideInstallService.persistCandidate(packageName)
  }

  fun isInstalledWithAptoide(packageName: String): Observable<Boolean> {
    if (isSplitInstalledWithAptoide(packageName)) {
      return Observable.just(true)
    }
    return aptoideInstallService.isInstalledWithAptoide(packageName)
  }

  private fun isSplitInstalledWithAptoide(packageName: String): Boolean {
    return false
  }
}