package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.View

interface WalletInstallView : View {

  fun showWalletInstallationView(appIcon: String)

  fun dismissDialog()
}