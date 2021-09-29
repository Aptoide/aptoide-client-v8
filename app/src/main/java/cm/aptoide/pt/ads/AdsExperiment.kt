package cm.aptoide.pt.ads

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import rx.Single

class AdsExperiment(private val abTestManager: ABTestManager) : RakamExperiment() {

  fun shouldLoadAds(): Single<Boolean> {
    return abTestManager.getExperiment(Companion.EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssigned = "control"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssigned = experiment.assignment
          }

          when (experimentAssigned) {
            "control" -> {
              return@flatMap Single.just(true)
            }
            else -> {
              return@flatMap Single.just(false)
            }
          }
        }
  }

  companion object {
    private const val EXPERIMENT_ID: String = "APP-608_VANILLA_ADS"
  }
}