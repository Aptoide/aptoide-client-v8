package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp

interface WalletInstallView : View {

  fun showWalletInstallationView(appIcon: String,
                                 walletApp: WalletApp)

  fun dismissDialog()
}