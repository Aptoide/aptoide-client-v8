package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.pt.download.DownloadAnalytics

class EarnAppcListAnalytics(private val downloadAnalytics: DownloadAnalytics) {

  fun sendNotEnoughSpaceErrorEvent(md5: String?) {
    downloadAnalytics.sendNotEnoughSpaceError(
        md5)
  }
}