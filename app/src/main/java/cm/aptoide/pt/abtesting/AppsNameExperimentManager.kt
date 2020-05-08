package cm.aptoide.pt.abtesting

import cm.aptoide.pt.abtesting.experiments.AppsNameExperiment
import rx.Completable

class AppsNameExperimentManager(private val appsNameExperiment: AppsNameExperiment) {

  fun setUpExperiment(): Completable {
    return appsNameExperiment.getAppsName()
        .flatMapCompletable { appsNameExperiment.recordImpression() }
  }
}