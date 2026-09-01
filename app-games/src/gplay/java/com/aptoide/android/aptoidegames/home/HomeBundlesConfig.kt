package com.aptoide.android.aptoidegames.home

import cm.aptoide.pt.feature_home.domain.Type

// These bundles don't depend on the store catalog, so the Play-distributed build hides them
internal val EXCLUDED_HOME_BUNDLE_TYPES: Set<Type> = setOf(
  Type.RTB_PROMO,
  Type.APP_COMING_SOON,
  Type.NEWS_ITEM,
  Type.NEW_APP,
  Type.NEW_APP_VERSION,
  Type.IN_GAME_EVENT,
)
