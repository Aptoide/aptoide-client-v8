package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.app.AppViewAnalytics
import rx.Single

class ApkfyExperiment(private val abTestManager: ABTestManager,
                      private val appViewAnalytics: AppViewAnalytics) : RakamExperiment() {
  private val EXPERIMENT_ID = "ASV-2053-SimilarApps"

  fun shouldShowApkfyInterstital(): Single<Boolean> {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "default"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "no_ads_on_first_session" -> {
              return@flatMap Single.just(false)
            }
            "default", "control" -> {
              return@flatMap Single.just(true)
            }
            else -> {
              return@flatMap Single.just(true)
            }
          }
        }
  }

}