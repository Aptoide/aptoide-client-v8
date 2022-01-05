package cm.aptoide.pt.app

import cm.aptoide.pt.promotions.Promotion
import cm.aptoide.pt.promotions.WalletApp

data class PromotionViewModel(
    var walletApp: WalletApp = WalletApp(),
    var promotions: List<Promotion> = ArrayList(),
    var appViewModel: AppViewModel? = null
)