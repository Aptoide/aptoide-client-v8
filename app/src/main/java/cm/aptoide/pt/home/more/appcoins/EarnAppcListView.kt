package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.promotions.WalletApp
import rx.Observable

interface EarnAppcListView : ListAppsView<RewardApp> {
  fun setupWallet(walletApp: WalletApp)
  fun showRootInstallWarningPopup(): Observable<Boolean>
  fun onWalletInstallClick(): Observable<Void>
  fun resumeDownload(): Observable<Void>
  fun pauseDownload(): Observable<Void>
  fun cancelDownload(): Observable<Void>
  fun updateState(walletApp: WalletApp)
  fun hideWalletArea()
}