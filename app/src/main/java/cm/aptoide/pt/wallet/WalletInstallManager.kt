package cm.aptoide.pt.wallet

import android.content.pm.PackageManager
import cm.aptoide.pt.utils.AptoideUtils
import rx.Observable

class WalletInstallManager(val configuration: WalletInstallConfiguration,
                           val packageManager: PackageManager) {

  fun getAppIcon(): Observable<String> {
    return Observable.just(AptoideUtils.SystemU.getApkIconPath(
        packageManager.getPackageInfo(configuration.appPackageName, 0)))
  }
}