package cm.aptoide.pt.abtesting.experiments

import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.abtesting.analytics.AppsNameAnalytics
import rx.Completable
import rx.Single
import java.util.*

class AppsNameExperiment(private val abTestManager: ABTestManager,
                         private val appsNameAnalytics: AppsNameAnalytics) : RakamExperiment() {

  private val EXPERIMENT_ID = "MOB-512-Apps_Name"

  private var assignment: String? = null

  fun getAppsName(): Single<String> {
    if (isTestingLanguage()) {
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
              "manager" -> {
                assignment = "manager"
                return@flatMap Single.just(assignment)
              }
              "my_apps" -> {
                assignment = "my_apps"
                return@flatMap Single.just(assignment)
              }
              "updates" -> {
                assignment = "updates"
                return@flatMap Single.just(assignment)
              }
              "apps", "control" -> {
                assignment = "control"
                return@flatMap Single.just(assignment)
              }
              else -> {
                return@flatMap Single.just(assignment)
              }
            }
          }
    } else
      return Single.just("control")
  }

  fun recordImpression(): Completable {
    if (isTestingLanguage()) {
      return abTestManager.getExperiment(EXPERIMENT_ID, type)
          .filter { !it.isExperimentOver && it.isPartOfExperiment && assignment != null }
          .doOnNext {
            assignment?.let { assign ->
              appsNameAnalytics.sendAbTestParticipatingEvent(assign)
            }
          }.toCompletable()
    }
    return Completable.complete()
  }


  fun recordConversion() {
    if (isTestingLanguage()) {
      abTestManager.getExperiment(EXPERIMENT_ID, type)
          .filter { !it.isExperimentOver && it.isPartOfExperiment && assignment != null }
          .doOnNext {
            assignment?.let { assign ->
              appsNameAnalytics.sendAbTestConvertingEvent(assignment!!)
            }
          }
    }
  }

  private fun isTestingLanguage(): Boolean {
    val language = Locale.getDefault().toString()
    return language == "pt_BR" || language.contains("es") || language.contains("en")
  }
}