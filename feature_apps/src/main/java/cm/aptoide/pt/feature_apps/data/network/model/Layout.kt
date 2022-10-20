package cm.aptoide.pt.feature_apps.data.network.model

import androidx.annotation.Keep

@Keep
enum class Layout {
  GRID, LIST, BRICK, GRAPHIC,

  // Action cards layouts
  APPC_INFO, CURATION_1, WALLET_ADS_OFFER, PROMO_GRAPHIC
}