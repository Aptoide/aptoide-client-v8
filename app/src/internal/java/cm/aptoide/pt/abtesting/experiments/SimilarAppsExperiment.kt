package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.app.AppViewAnalytics
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
            "default", "control" -> {
              appViewAnalytics.sendSimilarABTestGroupEvent(true)
              isControlGroup = true
              return@flatMap Single.just(false)
            }
            "appc_bundle" -> {
              appViewAnalytics.sendSimilarABTestGroupEvent(false)
              isControlGroup = false
              return@flatMap Single.just(true)
            }
            else -> {
              isControlGroup = true
              return@flatMap Single.just(false)
            }
          }
        }
  }

  fun recordImpression() {
    appViewAnalytics.sendSimilarABTestImpressionEvent(isControlGroup)
  }

  fun recordConversion() {
    appViewAnalytics.sendSimilarABTestConversionEvent(isControlGroup)
  }
}
