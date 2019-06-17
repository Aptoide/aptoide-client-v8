package cm.aptoide.pt.wallet

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.ActivityView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.wallet_install_activity.*
import rx.Observable
import javax.inject.Inject


class WalletInstallActivity : ActivityView(), WalletInstallView {

  @Inject
  lateinit var presenter: WalletInstallPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent.inject(this)
    setContentView(R.layout.wallet_install_activity)
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
    initStyling()
    progressView.visibility = View.GONE
    walletInstallSuccessViewGroup.visibility = View.GONE
    walletInstallViewGroup.visibility = View.VISIBLE

    ImageLoader.with(this).load(appIcon, appIconImageView)
  }

  override fun showInstallationSuccessView() {
    appIconImageView.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_check_orange_gradient_start))
    val message = getString(R.string.wallet_install_complete_title)
    messageTextView.text = message
    messageTextView.setSubstringTypeface(Pair(message, Typeface.BOLD))
    installCompleteMessage.text = getString(R.string.wallet_install_complete_body)
    progressView.visibility = View.GONE
    walletInstallSuccessViewGroup.visibility = View.VISIBLE
    walletInstallViewGroup.visibility = View.VISIBLE

  }

  override fun closeButtonClicked(): Observable<Void> {
    return RxView.clicks(closeButton)
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