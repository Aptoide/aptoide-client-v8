package cm.aptoide.pt.install

import android.content.Context

class ForegroundManager(val context: Context) {

  fun startDownloadService() {
    val intent = DownloadService.newInstanceForDownloads(context)
    context.startService(intent)
  }

}