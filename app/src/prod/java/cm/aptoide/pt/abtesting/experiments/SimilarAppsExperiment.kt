package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.app.AppViewAnalytics
import cm.aptoide.pt.logger.Logger
import rx.Completable
import rx.Single

open class SimilarAppsExperiment(private val abTestManager: ABTestManager,
                                 private val appViewAnalytics: AppViewAnalytics) :
    RakamExperiment() {
  private val EXPERIMENT_ID = "ASV-2053-SimilarApps"
  private var isControlGroup: Boolean = true

  fun shouldShowAppCoinsSimilarBundleFirst(): Single<Boolean> {
    return Single.just(true)
  }

  fun recordImpression(): Completable {
    Logger.getInstance()
        .d("SimilarAppsExperiment",
            "similar_apps_impression: $isControlGroup - NOT registering analytics")
    return Completable.complete()
  }

  fun recordConversion(): Completable {
    Logger.getInstance()
        .d("SimilarAppsExperiment",
            "similar_apps_conversion: $isControlGroup - NOT registering analytics")
    return Completable.complete()
  }
}
