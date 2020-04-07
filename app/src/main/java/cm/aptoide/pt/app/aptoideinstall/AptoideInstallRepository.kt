package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.install.AptoideInstallPersistence
import rx.Single
import java.util.*

class AptoideInstallRepository(val aptoideInstallPersistence: AptoideInstallPersistence) {

  private val aptoideInstallCandidates = ArrayList<String>()

  fun addAptoideInstallCandidate(packageName: String) {
    if (!aptoideInstallCandidates.contains(packageName)) {
      aptoideInstallCandidates.add(packageName)
    }
  }

  fun persistCandidate(packageName: String) {
    if (aptoideInstallCandidates.contains(packageName)) {
      aptoideInstallPersistence.insert(packageName)
      aptoideInstallCandidates.remove(packageName)
    }
  }

  fun isInstalledWithAptoide(packageName: String): Single<Boolean> {
    return aptoideInstallPersistence.isInstalledWithAptoide(packageName)
  }
}