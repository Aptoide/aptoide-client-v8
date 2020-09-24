package cm.aptoide.pt.gamification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.fragment.BaseDialogView
import com.jakewharton.rxbinding.view.RxView
import rx.Observable
import java.text.DecimalFormat
import javax.inject.Inject

class GamificationFragment : BaseDialogView(), GamificationView {


  @Inject
  lateinit var presenter: GamificationPresenter

  private lateinit var challengeOneUnlocked: View
  private lateinit var challengeOneComplete: View
  private lateinit var challengeTwoLocked: View
  private lateinit var challengeTwoTimeLocked: View
  private lateinit var challengeTwoUnlocked: View
  private lateinit var challengeRedeem: View

  private lateinit var appIcon: ImageView
  private lateinit var appName: TextView
  private lateinit var appRating: TextView

  private lateinit var timeMessage: TextView
  private lateinit var installButton: Button
  private lateinit var redeemButton: Button
  private lateinit var addressEditText: EditText


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    dialog?.window?.setBackgroundDrawableResource(R.drawable.transparent)

    return inflater.inflate(R.layout.gamification_dialog, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    challengeOneUnlocked = view.findViewById(R.id.challenge_1_unlocked)
    challengeOneComplete = view.findViewById(R.id.challenge_1_complete)
    challengeTwoLocked = view.findViewById(R.id.challenge_2_locked)
    challengeTwoTimeLocked = view.findViewById(R.id.challenge_2_time)
    challengeTwoUnlocked = view.findViewById(R.id.challenge_2_unlocked)
    challengeRedeem = view.findViewById(R.id.challenge_redeem)

    appIcon = view.findViewById(R.id.app_icon)
    appName = view.findViewById(R.id.app_name)
    appRating = view.findViewById(R.id.app_rating)

    timeMessage = challengeTwoTimeLocked.findViewById(R.id.time_message)
    installButton = challengeTwoUnlocked.findViewById(R.id.install_button)
    redeemButton = challengeRedeem.findViewById(R.id.redeem_button)
    addressEditText = challengeRedeem.findViewById(R.id.address_edit)

    attachPresenter(presenter)
  }

  override fun showInitialState() {
    challengeOneUnlocked.visibility = View.VISIBLE
    challengeOneComplete.visibility = View.GONE
    challengeTwoLocked.visibility = View.VISIBLE
    challengeTwoTimeLocked.visibility = View.GONE
    challengeTwoUnlocked.visibility = View.GONE
    challengeRedeem.visibility = View.GONE
  }

  override fun showSecondChallengeLocked(timeLeft: String) {
    challengeOneUnlocked.visibility = View.GONE
    challengeOneComplete.visibility = View.VISIBLE
    challengeTwoLocked.visibility = View.GONE
    challengeTwoTimeLocked.visibility = View.VISIBLE
    challengeTwoUnlocked.visibility = View.GONE
    challengeRedeem.visibility = View.GONE

    timeMessage.text = String.format("This challenge unlocks in %s", timeLeft)
  }

  override fun showSecondChallengeOpen(walletApp: WalletApp) {
    challengeOneUnlocked.visibility = View.GONE
    challengeOneComplete.visibility = View.VISIBLE
    challengeTwoLocked.visibility = View.GONE
    challengeTwoTimeLocked.visibility = View.GONE
    challengeTwoUnlocked.visibility = View.VISIBLE
    challengeRedeem.visibility = View.GONE
    setWalletInfo(walletApp)
  }

  override fun showRedeem() {
    challengeOneUnlocked.visibility = View.GONE
    challengeOneComplete.visibility = View.GONE
    challengeTwoLocked.visibility = View.GONE
    challengeTwoTimeLocked.visibility = View.GONE
    challengeTwoUnlocked.visibility = View.GONE
    challengeRedeem.visibility = View.VISIBLE
  }

  override fun clickOnInstall(): Observable<Boolean> {
    return RxView.clicks(installButton).map { true }
  }

  override fun clickOnRedeem(): Observable<String> {
    return RxView.clicks(redeemButton).map { addressEditText.text.toString() }
  }

  override fun dismiss() {
    super.dismiss()
  }

  private fun setWalletInfo(walletApp: WalletApp) {
    appName.text = walletApp.appName
    appRating.text = DecimalFormat("#.##").format(walletApp.rating)
    ImageLoader.with(context).load(walletApp.icon, appIcon)
  }
}
