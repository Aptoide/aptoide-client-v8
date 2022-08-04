package cm.aptoide.pt.download_view.presentation

import android.content.Context

class InstalledAppOpener(private val context: Context) {

  fun openInstalledApp(packageName: String) {
    val intentForPackage = context.packageManager.getLaunchIntentForPackage(packageName)
    context.startActivity(intentForPackage)
  }
}