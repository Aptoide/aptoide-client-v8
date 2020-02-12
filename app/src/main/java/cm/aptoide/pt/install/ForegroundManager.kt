package cm.aptoide.pt.install

import android.content.Context

class ForegroundManager(val context: Context) {

  fun startDownloadForeground() {
    val intent = InstallService.newInstanceForDownloads(context)
    context.startService(intent)
  }

}