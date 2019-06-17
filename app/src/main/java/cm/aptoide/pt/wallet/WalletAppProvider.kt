package cm.aptoide.pt.wallet

import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.app.AppCenter
import cm.aptoide.pt.view.app.DetailedAppRequestResult
import rx.Observable

class WalletAppProvider(val appCenter: AppCenter) {

  fun getWalletApp(): Observable<WalletApp> {
    return appCenter.loadDetailedApp("com.appcoins.wallet", "catappult")
        .toObservable()
        .map { this.mapToWalletApp(it) }
  }

  private fun mapToWalletApp(result: DetailedAppRequestResult): WalletApp {
    if (result.hasError() || result.isLoading) return WalletApp()
    val app = result.detailedApp
    return WalletApp(null, false, app.name, app.icon, app.id,
        app.packageName, app.md5, app.versionCode, app.versionName,
        app.path, app.pathAlt, app.obb, app.size)
  }
}
