package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.pt.database.accessors.AptoideInstallAccessor
import rx.Observable
import java.util.*

class AptoideInstallService(val aptoideInstallAccessor: AptoideInstallAccessor) {

  private val aptoideInstallCandidates = ArrayList<String>()

  fun addAptoideInstallCandidate(packageName: String) {
    if (!aptoideInstallCandidates.contains(packageName)) {
      aptoideInstallCandidates.add(packageName)
    }
  }

  fun persistCandidate(packageName: String) {
    if (aptoideInstallCandidates.contains(packageName)) {
      aptoideInstallAccessor.insert(packageName)
      aptoideInstallCandidates.remove(packageName)
    }
  }

  fun isInstalledWithAptoide(packageName: String): Observable<Boolean> {
    return aptoideInstallAccessor.isInstalledWithAptoide(packageName)
  }
}