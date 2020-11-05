package cm.aptoide.pt.download.view

import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener


fun DownloadEventListener.Action.toDownloadEvent(): DownloadEvent {
  return when (this.type) {
    DownloadEventListener.Action.Type.CANCEL -> DownloadEvent.CANCEL
    DownloadEventListener.Action.Type.RESUME -> DownloadEvent.RESUME
    DownloadEventListener.Action.Type.PAUSE -> DownloadEvent.PAUSE
  }
}

enum class DownloadEvent {
  INSTALL, RESUME, PAUSE, CANCEL, GENERIC_ERROR, OUT_OF_SPACE_ERROR
}