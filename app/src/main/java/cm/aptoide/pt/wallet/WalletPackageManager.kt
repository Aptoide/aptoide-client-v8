package cm.aptoide.pt.wallet

import android.content.Intent
import android.content.pm.PackageManager


class WalletPackageManager(private val packageManager: PackageManager) {

  private val walletPackage = "com.appcoins.wallet"

  fun isThereAPackageToProcessAPPCPayments(): Boolean {
    val serviceIntent = Intent("com.appcoins.wallet.iab.action.BIND")
    val intentServices = packageManager.queryIntentServices(serviceIntent, 0)
    return intentServices.isNotEmpty()
  }


  fun isWalletInstalled(): Boolean {
    return try {
      packageManager.getPackageInfo(walletPackage, 0)
      true
    } catch (e: PackageManager.NameNotFoundException) {
      false
    }
  }

  fun getWalletPackage(): String {
    return walletPackage
  }

}