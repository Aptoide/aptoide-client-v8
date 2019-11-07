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
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "default"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "control" -> {
              appViewAnalytics.sendSimilarABTestGroupEvent(true)
              isControlGroup = true
              return@flatMap Single.just(false)
            }
            "appc_bundle" -> {
              appViewAnalytics.sendSimilarABTestGroupEvent(false)
              isControlGroup = false
              return@flatMap Single.just(true)
            }
            "default" -> {
              isControlGroup = true
              return@flatMap Single.just(false)
            }
            else -> {
              isControlGroup = true
              return@flatMap Single.just(false)
            }
          }
        }
  }

  fun recordImpression(): Completable {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .filter { !it.isExperimentOver && it.isPartOfExperiment }
        .toCompletable()
        .doOnCompleted { appViewAnalytics.sendSimilarABTestImpressionEvent(isControlGroup) }
  }

  fun recordConversion(): Completable {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .filter { !it.isExperimentOver && it.isPartOfExperiment }
        .toCompletable()
        .doOnCompleted { appViewAnalytics.sendSimilarABTestConversionEvent(isControlGroup) }
  }
}
