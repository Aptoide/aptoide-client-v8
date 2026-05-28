package cm.aptoide.pt.download_view.presentation

import android.content.Context
import android.content.Intent

class InstalledAppOpener(private val context: Context) {

  fun openInstalledApp(packageName: String) {
    val intentForPackage = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intentForPackage != null) {
      intentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intentForPackage)
    }
  }
}
