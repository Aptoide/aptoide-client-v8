package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallAnalytics
import rx.Completable
import rx.Single

class AptoideInstallExperiment(private val abTestManager: ABTestManager,
                               private val aptoideInstallAnalytics: AptoideInstallAnalytics) :
    RakamExperiment() {

  private val EXPERIMENT_ID = "MOB-238-Installed_With_Aptoide"

  private var assignment: String? = null

  fun shouldShowAptoideInstallFeature(): Single<Boolean> {
    if (assignment != null) {
      return Single.just(assignment).map { assignment.equals("b") }
    }
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "default"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "show_installed" -> {
              assignment = "b"
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


  fun recordImpression(): Completable {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .filter { !it.isExperimentOver && it.isPartOfExperiment && assignment != null }
        .toCompletable()
        .doOnCompleted {
          assignment?.let { assign ->
            aptoideInstallAnalytics.sendAbTestParticipatingEvent(assign)
          }
        }
  }

  fun recordConversion(): Completable {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .filter { !it.isExperimentOver && it.isPartOfExperiment && assignment != null }
        .toCompletable()
        .doOnCompleted {
          assignment?.let { assign ->
            aptoideInstallAnalytics.sendAbTestConvertingEvent(assignment!!)
          }
        }
  }
}