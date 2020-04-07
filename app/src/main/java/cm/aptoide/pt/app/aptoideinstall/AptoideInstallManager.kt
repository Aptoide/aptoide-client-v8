package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.abtesting.experiments.AptoideInstallExperiment
import cm.aptoide.pt.install.InstalledRepository
import rx.Completable
import rx.Single

class AptoideInstallManager(val installedRepository: InstalledRepository,
                            val aptoideInstallRepository: AptoideInstallRepository,
                            val aptoideInstallExperiment: AptoideInstallExperiment) {

  fun addAptoideInstallCandidate(packageName: String) {
    aptoideInstallRepository.addAptoideInstallCandidate(packageName)
  }

  fun persistCandidate(packageName: String) {
    aptoideInstallRepository.persistCandidate(packageName)
  }

  fun isInstalledWithAptoide(packageName: String): Single<Boolean> {
    return aptoideInstallExperiment.shouldShowAptoideInstallFeature()
        .flatMap { shouldShow ->
          if (shouldShow) {
            if (isSplitInstalledWithAptoide(packageName)) {
              return@flatMap Single.just(true)
            }
            return@flatMap aptoideInstallRepository.isInstalledWithAptoide(packageName)
          }
          return@flatMap Single.just(false)
        }
  }

  fun sendImpressionEvent(): Completable {
    return aptoideInstallExperiment.recordImpression()
  }

  fun sendConversionEvent(): Completable {
    return aptoideInstallExperiment.recordConversion()
  }

  private fun isSplitInstalledWithAptoide(packageName: String): Boolean {
    return false
  }
}