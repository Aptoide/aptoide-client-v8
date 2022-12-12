package cm.aptoide.pt.feature_home.data.network.model

import androidx.annotation.Keep

@Keep
@Suppress("unused")
enum class Layout {
  GRID,
  LIST,
  BRICK,
  GRAPHIC,
  PUBLISHER_TAKEOVER,
  CAROUSEL,
  CAROUSEL_LARGE,

  // Action cards layouts
  APPC_INFO,
  CURATION_1,
  WALLET_ADS_OFFER,
  PROMO_GRAPHIC
}