package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import rx.Single

class ApkfyGamificationExperiment(val abTestManager: ABTestManager) : RakamExperiment() {

  private val EXPERIMENT_ID = "MOB-XXX-Gamification-Apkfy"

  private var assignment: String? = null

  fun shouldShowGamification(): Single<Boolean> {
    if (assignment != null) {
      return Single.just(assignment).map { assignment.equals("gamification") }
    }
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "default"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "gamification" -> {
              assignment = "gamification"
              return@flatMap Single.just(true)
            }
            "default", "control" -> {
              assignment = "control"
              return@flatMap Single.just(false)
            }
            else -> {
              return@flatMap Single.just(false)
            }
          }
        }
  }
}