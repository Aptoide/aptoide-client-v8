package cm.aptoide.pt.install

import io.reactivex.Single

interface InstalledApps {

  fun getInstalledAppsNames(): Single<List<String>>

}
