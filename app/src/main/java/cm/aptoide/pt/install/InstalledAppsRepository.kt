package cm.aptoide.pt.install

import io.reactivex.Single

interface InstalledAppsRepository {

  fun getInstalledAppsNames(): Single<List<String>>

}
