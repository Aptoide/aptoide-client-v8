package cm.aptoide.pt.gamification

import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import rx.Observable

interface GamificationView : View {

  fun showInitialState()

  fun showSecondChallengeLocked(timeLeft: String)

  fun showSecondChallengeOpen(walletApp: WalletApp)

  fun showRedeem()

  fun clickOnInstall(): Observable<Boolean>

  fun clickOnRedeem(): Observable<String>

  fun dismiss()
}