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
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.ActivityView
import kotlinx.android.synthetic.main.wallet_install_activity.*
import kotlinx.android.synthetic.main.wallet_install_download_view.*
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

  override fun showWalletInstallationView(appIcon: String,
                                          walletApp: WalletApp) {
    ImageLoader.with(this).load(appIcon, appIconImageView)
    val downloadModel = walletApp.downloadModel
    if (downloadModel!!.isDownloading) {
      setDownloadProgress(downloadModel)
    } else {
      progressView.visibility = View.GONE
      walletInstallViewGroup.visibility = View.VISIBLE
    }
  }

  private fun setDownloadProgress(downloadModel: DownloadModel) {
    when (downloadModel.downloadState) {
      DownloadModel.DownloadState.ACTIVE -> {
        wallet_download_progress_bar.setIndeterminate(false)
        wallet_download_progress_bar.setProgress(downloadModel
            .progress)
        wallet_download_progress_number.setText(downloadModel
            .progress.toString() + "%")
        wallet_download_cancel_button.setVisibility(View.GONE)
      }
      DownloadModel.DownloadState.INDETERMINATE, DownloadModel.DownloadState.INSTALLING, DownloadModel.DownloadState.COMPLETE -> {
        wallet_download_progress_bar.setIndeterminate(true)
        wallet_download_cancel_button.setVisibility(View.GONE)
      }
      DownloadModel.DownloadState.ERROR -> showErrorMessage(
          getString(R.string.error_occured))
      DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR -> showErrorMessage(
          getString(R.string.out_of_space_dialog_title))
      else -> {
        throw IllegalArgumentException("Invalid download state")
      }
    }
  }

  private fun showErrorMessage(errorMessage: String?) {
    wallet_download_download_state.text = errorMessage
  }

  override fun dismissDialog() {
    finish()
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


}