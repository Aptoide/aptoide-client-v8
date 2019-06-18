package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import rx.Observable

interface WalletInstallView : View {

  fun showWalletInstallationView(appIcon: String,
                                 walletApp: WalletApp)

  fun dismissDialog()

  fun showRootInstallWarningPopup(): Observable<Boolean>?
  fun showIndeterminateDownload()
}