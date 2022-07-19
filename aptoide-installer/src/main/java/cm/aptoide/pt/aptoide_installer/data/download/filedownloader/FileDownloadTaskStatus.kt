package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import cm.aptoide.pt.downloadmanager.AppDownloadStatus.AppDownloadState
import cm.aptoide.pt.downloadmanager.FileDownloadCallback
import cm.aptoide.pt.downloadmanager.FileDownloadProgressResult

/**
 * Created by filipegoncalves on 8/29/18.
 */
class FileDownloadTaskStatus : FileDownloadCallback {
  private var appDownloadState: AppDownloadState
  private var downloadProgress: FileDownloadProgressResult
  private var md5: String
  private var error: Throwable?

  constructor(
    appDownloadState: AppDownloadState,
    downloadProgress: FileDownloadProgressResult, md5: String
  ) {
    this.appDownloadState = appDownloadState
    this.downloadProgress = downloadProgress
    this.md5 = md5
    this.error = null
  }

  constructor(appDownloadState: AppDownloadState, md5: String, error: Throwable?) {
    this.appDownloadState = appDownloadState
    this.md5 = md5
    this.error = error
    this.downloadProgress = FileDownloadProgressResult(0, 0)
  }

  override fun getDownloadProgress(): FileDownloadProgressResult {
    return downloadProgress
  }

  override fun getDownloadState(): AppDownloadState {
    return appDownloadState
  }

  override fun getMd5(): String {
    return md5
  }

  override fun hasError(): Boolean {
    return error != null
  }

  override fun getError(): Throwable? {
    return error
  }

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o == null || javaClass != o.javaClass) return false
    val that = o as FileDownloadTaskStatus
    return md5 == that.getMd5()
  }
}