package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.abtesting.experiments.AptoideInstallExperiment
import cm.aptoide.pt.install.InstalledRepository
import rx.Completable
import rx.Observable

class AptoideInstallManager(val installedRepository: InstalledRepository,
                            val aptoideInstallService: AptoideInstallService,
                            val aptoideInstallExperiment: AptoideInstallExperiment) {

  fun addAptoideInstallCandidate(packageName: String) {
    aptoideInstallService.addAptoideInstallCandidate(packageName)
  }

  fun persistCandidate(packageName: String) {
    aptoideInstallService.persistCandidate(packageName)
  }

  fun isInstalledWithAptoide(packageName: String): Observable<Boolean> {
    return aptoideInstallExperiment.shouldShowAptoideInstallFeature()
        .flatMapObservable { shouldShow ->
          if (shouldShow) {
            if (isSplitInstalledWithAptoide(packageName)) {
              Observable.just(true)
            }
            aptoideInstallService.isInstalledWithAptoide(packageName)
          }
          Observable.just(false)
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