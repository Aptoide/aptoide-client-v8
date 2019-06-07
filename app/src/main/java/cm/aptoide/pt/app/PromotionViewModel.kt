package cm.aptoide.pt.app

import cm.aptoide.pt.promotions.Promotion
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.app.DetailedApp

data class PromotionViewModel(
    var walletApp: WalletApp = WalletApp(),
    var promotions: List<Promotion> = ArrayList(),
    var appDownloadModel: DownloadModel? = null,
    var app: DetailedApp? = null,
    var isAppMigrated: Boolean = false
)