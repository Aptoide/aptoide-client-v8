package cm.aptoide.pt.download.view

import android.content.Context
import android.view.View
import android.widget.Button
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.pt.R
import rx.subjects.PublishSubject

/**
 * Used to help updating a Download button + DownloadProgressView combo when using a [Download] model
 */
class DownloadViewStatusHelper(val context: Context) {

  /**
   * This is useful in case of RecyclerViews where event listeners + subjects must be used.
   */
  fun setupListeners(
    download: Download, downloadClickSubject: PublishSubject<DownloadClick>,
    installButton: Button,
    downloadProgressView: DownloadProgressView
  ) {
    installButton.setOnClickListener {
      downloadClickSubject.onNext(DownloadClick(download, DownloadEvent.INSTALL))
    }

    downloadProgressView.setEventListener(object : DownloadEventListener {
      override fun onActionClick(action: DownloadEventListener.Action) {
        when (action.type) {
          DownloadEventListener.Action.Type.CANCEL -> downloadClickSubject.onNext(
            DownloadClick(download, DownloadEvent.CANCEL)
          )
          DownloadEventListener.Action.Type.RESUME -> downloadClickSubject.onNext(
            DownloadClick(download, DownloadEvent.RESUME)
          )
          DownloadEventListener.Action.Type.PAUSE -> downloadClickSubject.onNext(
            DownloadClick(download, DownloadEvent.PAUSE)
          )
        }
      }
    })
  }

  fun setDownloadStatus(
    download: Download, installButton: Button,
    downloadProgressView: DownloadProgressView,
    downloadClickSubject: PublishSubject<DownloadClick>
  ) {
    download.downloadModel?.let { downloadModel ->
      if (downloadModel.downloadState == DownloadStatusModel.DownloadState.GENERIC_ERROR) {
        downloadClickSubject.onNext(
          DownloadClick(download, DownloadEvent.GENERIC_ERROR)
        )
      } else if (downloadModel.downloadState == DownloadStatusModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
        downloadClickSubject.onNext(DownloadClick(download, DownloadEvent.OUT_OF_SPACE_ERROR))
      }
    }
    download.downloadModel?.let { downloadModel ->
      if (downloadModel.isDownloadingOrInstalling()) {
        setDownloadState(downloadProgressView, downloadModel.progress, downloadModel.downloadState)
        installButton.visibility = View.GONE
        downloadProgressView.visibility = View.VISIBLE
      } else {
        setDownloadButtonText(downloadModel.action, installButton)
        downloadProgressView.visibility = View.GONE
        installButton.visibility = View.VISIBLE
      }
    }
  }

  private fun setDownloadButtonText(
    action: DownloadStatusModel.Action,
    installButton: Button
  ) {
    when (action) {
      DownloadStatusModel.Action.UPDATE -> installButton.text =
        context.getString(R.string.appview_button_update)
      DownloadStatusModel.Action.INSTALL -> installButton.text =
        context.getString(R.string.appview_button_install)
      DownloadStatusModel.Action.OPEN -> installButton.text =
        context.getString(R.string.appview_button_open)
      DownloadStatusModel.Action.DOWNGRADE -> installButton.text =
        context.getString(R.string.appview_button_downgrade)
      DownloadStatusModel.Action.MIGRATE -> installButton.text =
        context.getString(R.string.promo_update2appc_appview_update_button)
    }
  }

  private fun setDownloadState(
    downloadProgressView: DownloadProgressView,
    progress: Int,
    downloadState: DownloadStatusModel.DownloadState
  ) {
    when (downloadState) {
      DownloadStatusModel.DownloadState.ACTIVE ->
        downloadProgressView.startDownload()
      DownloadStatusModel.DownloadState.INSTALLING ->
        downloadProgressView.startInstallation()
      DownloadStatusModel.DownloadState.PAUSE ->
        downloadProgressView.pauseDownload()
      DownloadStatusModel.DownloadState.IN_QUEUE,
      DownloadStatusModel.DownloadState.STANDBY ->
        downloadProgressView.reset()
      DownloadStatusModel.DownloadState.GENERIC_ERROR,
      DownloadStatusModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR -> {
      }
    }
    downloadProgressView.setProgress(progress)
  }

}