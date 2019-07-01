package cm.aptoide.pt.wallet

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.logger.Logger
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.utils.GenericDialogs
import cm.aptoide.pt.view.ActivityView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.wallet_install_activity.*
import kotlinx.android.synthetic.main.wallet_install_download_view.*
import rx.Observable
import javax.inject.Inject


class WalletInstallActivity : ActivityView(), WalletInstallView {

  @Inject
  lateinit var presenter: WalletInstallPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent.inject(this)
    setContentView(R.layout.wallet_install_activity)
    initStyling()
    attachPresenter(presenter)
  }

  private fun initStyling() {
    val walletAppName = getString(R.string.wallet_install_appcoins_wallet)
    val message = getString(R.string.wallet_install_request_message_body, walletAppName)
    messageTextView.text = message
    messageTextView.setSubstringTypeface(Pair(walletAppName, Typeface.BOLD))
  }

  override fun showWalletInstallationView(appIcon: String?,
                                          walletApp: WalletApp) {
    progressView.visibility = View.GONE
    walletInstallSuccessViewGroup.visibility = View.GONE
    closeButton.visibility = View.GONE

    appIcon?.let {
      ImageLoader.with(this).load(appIcon, appIconImageView)
    }
    val downloadModel = walletApp.downloadModel
    Logger.getInstance()
        .d("WalletInstallActivity", "download state is " + downloadModel!!.downloadState)
    if (downloadModel.isDownloading) {
      setDownloadProgress(downloadModel)
    } else {
      wallet_install_download_view.visibility = View.GONE
      progressView.visibility = View.GONE
      walletInstallViewGroup.visibility = View.VISIBLE
    }
  }

  private fun setDownloadProgress(downloadModel: DownloadModel) {
    wallet_install_download_view.visibility = View.VISIBLE
    when (downloadModel.downloadState) {
      DownloadModel.DownloadState.ACTIVE,
      DownloadModel.DownloadState.PAUSE -> {
        walletInstallViewGroup.visibility = View.VISIBLE
        wallet_download_cancel_button.visibility = View.VISIBLE
        wallet_download_progress_bar.isIndeterminate = false
        wallet_download_progress_bar.progress = downloadModel
            .progress
        wallet_download_progress_number.text = downloadModel
            .progress.toString() + "%"
        wallet_download_progress_number.visibility = View.VISIBLE
      }
      DownloadModel.DownloadState.INDETERMINATE -> {
        wallet_download_progress_bar.isIndeterminate = true
        walletInstallViewGroup.visibility = View.VISIBLE
        wallet_download_cancel_button.visibility = View.VISIBLE
      }
      DownloadModel.DownloadState.INSTALLING,
      DownloadModel.DownloadState.COMPLETE -> {
        wallet_download_progress_bar.isIndeterminate = true
        wallet_download_progress_number.visibility = View.GONE
        walletInstallViewGroup.visibility = View.VISIBLE
        wallet_download_cancel_button.visibility = View.INVISIBLE
      }
      DownloadModel.DownloadState.ERROR -> showErrorMessage(
          getString(R.string.error_occured))
      DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR -> showErrorMessage(
          getString(R.string.out_of_space_dialog_title))
      else -> {
        throw IllegalArgumentException("Invalid download state" + downloadModel.downloadState)
      }
    }
  }

  private fun showErrorMessage(errorMessage: String) {
    wallet_download_download_state.text = errorMessage
  }

  override fun showInstallationSuccessView() {
    showSuccessView(getString(R.string.wallet_install_complete_title),
        getString(R.string.wallet_install_complete_body))
  }

  override fun showWalletInstalledAlreadyView() {
    showSuccessView(getString(R.string.wallet_install_request_already_installed_title),
        getString(R.string.wallet_install_request_already_installed_body))
  }

  private fun showSuccessView(title: String, body: String) {
    appIconImageView.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_check_orange_gradient_start))
    messageTextView.text = title
    messageTextView.setSubstringTypeface(Pair(title, Typeface.BOLD))
    installCompleteMessage.text = body
    progressView.visibility = View.GONE
    walletInstallSuccessViewGroup.visibility = View.VISIBLE
    closeButton.visibility = View.VISIBLE
    walletInstallViewGroup.visibility = View.VISIBLE
    wallet_install_download_view.visibility = View.GONE

  }

  override fun closeButtonClicked(): Observable<Void> {
    return RxView.clicks(closeButton)
  }

  override fun dismissDialog() {
    finish()
  }

  override fun showSdkErrorView() {
    sdkErrorViewGroup.visibility = View.VISIBLE
    progressView.visibility = View.GONE
    closeButton.visibility = View.VISIBLE
    walletInstallSuccessViewGroup.visibility = View.GONE
    walletInstallViewGroup.visibility = View.INVISIBLE

  }

  override fun showRootInstallWarningPopup(): Observable<Boolean> {
    return GenericDialogs.createGenericYesNoCancelMessage(applicationContext, null,
        resources.getString(R.string.root_access_dialog))
        .map { response -> response.equals(GenericDialogs.EResponse.YES) }
  }


  /**
   * Sets the specified Typeface Style on the first instance of the specified substring(s)
   * @param one or more [Pair] of [String] and [Typeface] style (e.g. BOLD, ITALIC, etc.)
   */
  fun TextView.setSubstringTypeface(vararg textsToStyle: Pair<String, Int>) {
    val spannableString = SpannableString(this.text)
    for (textToStyle in textsToStyle) {
      val startIndex = this.text.toString().indexOf(textToStyle.first)
      val endIndex = startIndex + textToStyle.first.length

      if (startIndex >= 0) {
        spannableString.setSpan(
            StyleSpan(textToStyle.second),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
      }
    }
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
  }

  override fun showIndeterminateDownload() {
    wallet_install_download_view.visibility = View.VISIBLE
    wallet_download_progress_bar.isIndeterminate = true
    walletInstallViewGroup.visibility = View.VISIBLE
  }

  override fun cancelDownloadButtonClicked(): Observable<Void> {
    return RxView.clicks(wallet_download_cancel_button)
  }

  override fun showDownloadState(downloadModel: DownloadModel) {
    if (downloadModel.isDownloadingOrInstalling) {
      wallet_install_download_view.visibility = View.VISIBLE
      setDownloadProgress(downloadModel)
    } else {
      wallet_install_download_view.visibility = View.GONE
      progressView.visibility = View.GONE
      walletInstallViewGroup.visibility = View.VISIBLE
    }
  }
}