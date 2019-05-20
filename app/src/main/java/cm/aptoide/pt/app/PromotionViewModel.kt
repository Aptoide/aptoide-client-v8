package cm.aptoide.pt.app

import cm.aptoide.pt.promotions.Promotion
import cm.aptoide.pt.promotions.WalletApp

data class PromotionViewModel(
    var walletApp: WalletApp,
    var promotion: Promotion,
    var isAppViewAppInstalled: Boolean
)