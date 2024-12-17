package cm.aptoide.pt.installer.fetch

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

interface FetchFileDownloadListener : FetchListener {
  override fun onAdded(download: Download) {}

  override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}

  override fun onWaitingNetwork(download: Download) {}

  override fun onCompleted(download: Download) {}

  override fun onError(
    download: Download,
    error: Error,
    throwable: Throwable?
  ) {
  }

  override fun onDownloadBlockUpdated(
    download: Download,
    downloadBlock: DownloadBlock,
    totalBlocks: Int
  ) {
  }

  override fun onStarted(
    download: Download,
    downloadBlocks: List<DownloadBlock>,
    totalBlocks: Int
  ) {
  }

  override fun onProgress(
    download: Download,
    etaInMilliSeconds: Long,
    downloadedBytesPerSecond: Long
  ) {
  }

  override fun onPaused(download: Download) {}

  override fun onResumed(download: Download) {}

  override fun onCancelled(download: Download) {}

  override fun onRemoved(download: Download) {}

  override fun onDeleted(download: Download) {}
}
