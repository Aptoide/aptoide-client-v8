package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_home.domain.Type

/**
 * Where the home native ads go in the bundle list, as pure decisions so they can be unit tested.
 * Both ads are rendered *before* the bundle at their index, inside that bundle's lazy item.
 */
object HomeNativeAdLayout {

  /** Bundle types that are themselves sponsored or reward surfaces; MAX ads never sit next to them. */
  val SPONSORED_TYPES: Set<Type> = setOf(
    Type.RTB_PROMO,
    Type.APPC_BANNER,
    Type.PLAY_AND_EARN,
    Type.PUBLISHER_TAKEOVER,
  )

  /**
   * Whether the bundle at [index] is inside the visible range [firstVisible]..[lastVisible]
   * widened by [margin] bundles on each side. Home slot activity follows this live range (and the
   * host lifecycle) instead of a reached-once trigger, so an ad scrolled far away stops
   * retrying and reloading until it is near again.
   */
  fun isNearVisible(index: Int?, firstVisible: Int, lastVisible: Int, margin: Int = 1): Boolean =
    NativeAdVisibility.isIndexNearVisible(index, firstVisible, lastVisible, margin)

  /**
   * The first slot: the first index at or after the configured [position] (clamped to the list)
   * whose bundle, and the bundle above it, is not sponsored; null when no such spot exists, so
   * neither home ad ever sits next to an RTB, bonus, Play and Earn or takeover bundle.
   */
  fun firstIndex(bundleTypes: List<Type>, position: Int): Int? {
    if (bundleTypes.isEmpty()) return null
    val start = position.coerceIn(0, bundleTypes.size - 1)
    for (index in start until bundleTypes.size) {
      if (isClear(bundleTypes, index)) return index
    }
    return null
  }

  private fun isClear(bundleTypes: List<Type>, index: Int): Boolean {
    val below = bundleTypes[index]
    val above = bundleTypes.getOrNull(index - 1)
    return below !in SPONSORED_TYPES && (above == null || above !in SPONSORED_TYPES)
  }

  /**
   * The deeper slot: the first index at or after [requestedIndex] that keeps at least [minGap]
   * bundles after the first ad and touches no sponsored bundle on either side, or null when the
   * list has no such spot. With no first ad, the gap is measured from the top of the list.
   */
  fun deepIndex(
    bundleTypes: List<Type>,
    firstIndex: Int?,
    requestedIndex: Int,
    minGap: Int,
  ): Int? {
    if (bundleTypes.isEmpty()) return null
    val earliest = maxOf(requestedIndex, (firstIndex ?: -1) + minGap.coerceAtLeast(1))
    for (index in earliest until bundleTypes.size) {
      if (isClear(bundleTypes, index)) return index
    }
    return null
  }
}
