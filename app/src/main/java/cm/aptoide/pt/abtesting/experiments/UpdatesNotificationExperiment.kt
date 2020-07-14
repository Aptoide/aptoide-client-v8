package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import rx.Single

class UpdatesNotificationExperiment(private val abTestManager: ABTestManager) :
    RakamExperiment() {

  private val EXPERIMENT_ID = "MOB-657-Updates-Notification"

  private var assignment: String? = null


  fun getConfiguration(): Single<String> {
    if (assignment != null) {
      return Single.just(assignment)
    }
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "control"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "wifi" -> {
              assignment = "wifi"
              return@flatMap Single.just(assignment)
            }
            "charge" -> {
              assignment = "charge"
              return@flatMap Single.just(assignment)
            }
            "wifi_charge" -> {
              assignment = "wifi_charge"
              return@flatMap Single.just(assignment)
            }
            "design" -> {
              assignment = "design"
              return@flatMap Single.just(assignment)
            }
            "all" -> {
              assignment = "all"
              return@flatMap Single.just(assignment)
            }
            "control" -> {
              assignment = "control"
              return@flatMap Single.just(assignment)
            }
            else -> {
              return@flatMap Single.just(assignment)
            }
          }
        }
  }
}