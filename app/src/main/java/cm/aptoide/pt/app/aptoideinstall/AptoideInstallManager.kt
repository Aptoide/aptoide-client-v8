package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.install.InstalledRepository
import rx.Single

class AptoideInstallManager(val installedRepository: InstalledRepository,
                            val aptoideInstallRepository: AptoideInstallRepository) {

  fun addAptoideInstallCandidate(packageName: String) {
    aptoideInstallRepository.addAptoideInstallCandidate(packageName)
  }

  fun persistCandidate(packageName: String) {
    aptoideInstallRepository.persistCandidate(packageName)
  }

  fun isInstalledWithAptoide(packageName: String): Single<Boolean> {
    if (isSplitInstalledWithAptoide(packageName)) {
      return Single.just(true)
    }
    return aptoideInstallRepository.isInstalledWithAptoide(packageName)
  }

  private fun isSplitInstalledWithAptoide(packageName: String): Boolean {
    return false
  }
}