package com.aptoide.android.aptoidegames.ads.native_ads

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

/**
 * Where a native ad goes in a browsing list, as a pure decision so it can be unit tested.
 *
 * The ad is inserted after [position] organic items. Lists shorter than that never show it, so
 * a short category or collection is not an ad with one app above it. The organic items keep
 * their original indices (rank, analytics item position) on both sides of the ad.
 */
object NativeAdListPlan {

  /** Number of organic items before the ad, or null when the list gets no ad. */
  fun adIndex(itemCount: Int, position: Int, hasAd: Boolean): Int? {
    if (!hasAd) return null
    val after = position.coerceAtLeast(1)
    return if (itemCount >= after) after else null
  }

  /** Whether the list is long enough for the slot to be worth a request at all. */
  fun shouldRequest(itemCount: Int, position: Int): Boolean =
    itemCount >= position.coerceAtLeast(1)

  /**
   * Whether a list slot should be active: the host is resumed, the list is long enough for the
   * *effective* position (the remote value once known, the default before that) and the slot's
   * lazy index is near the list's live visible range ([nearVisible]). A list that shrinks below
   * the threshold, a screen that is backgrounded, or a list scrolled far past the slot deactivates
   * it, so deferred retries and expiry reloads wait until it is near again.
   */
  fun shouldBeActive(
    resumed: Boolean,
    itemCount: Int,
    configuredPosition: Int?,
    defaultPosition: Int,
    nearVisible: Boolean,
  ): Boolean =
    resumed && nearVisible && shouldRequest(itemCount, configuredPosition ?: defaultPosition)

  /** Lazy index the ad occupies: right after the configured number of organic rows. */
  fun slotIndex(configuredPosition: Int?, defaultPosition: Int): Int =
    (configuredPosition ?: defaultPosition).coerceAtLeast(1)

  fun adKey(placement: NativeAdPlacement): String = "native_ad_${placement.flagPrefix}"

  /**
   * Stable, unique lazy-list keys for organic rows: the app's package name, with a numbered
   * suffix on repeats so a list that contains the same app twice cannot crash the LazyColumn
   * with a duplicate key. Inserting or removing the ad never changes an organic row's key.
   */
  fun uniqueKeys(ids: List<String>): List<String> {
    val seen = HashMap<String, Int>(ids.size)
    return ids.map { id ->
      val count = seen.merge(id, 1, Int::plus) ?: 1
      if (count == 1) id else "$id#$count"
    }
  }

  const val AD_CONTENT_TYPE = "native_ad"
}

/** Whether a slot is close enough to the viewport to be worth an ad request, recomputed on every layout. */
object NativeAdVisibility {
  /**
   * [top] is the slot's top in root coordinates and [height] its measured height (zero until an
   * ad is loaded). The slot is near when it starts above the bottom of the screen plus
   * [preload] and has not been scrolled past by more than [preload]: this is a live condition, so
   * a slot scrolled away stops being near, not "visible once".
   */
  fun isNearViewport(top: Float, height: Float, screenHeight: Float, preload: Float): Boolean =
    top < screenHeight + preload && top + height > -preload

  /**
   * Whether the lazy-list item at [index] lies within [firstVisible]..[lastVisible] widened by
   * [margin] items. [index] may refer to an item that is not composed yet (an unloaded ad slot):
   * its position in the list is known from the configured position.
   */
  fun isIndexNearVisible(index: Int?, firstVisible: Int, lastVisible: Int, margin: Int = 1): Boolean =
    index != null && index >= firstVisible - margin && index <= lastVisible + margin
}

/**
 * The explicit browsing context rule: a list may carry a native ad only when it is an ordinary
 * browsing surface (one of the browse placements) and is not an Aptoide RTB list. Updates, RTB,
 * bonus / reward and install-management screens do not pass an ad ViewModel at all; this guards
 * the one shared renderer (`AppsList`) that RTB also uses.
 */
object BrowseAdEligibility {
  fun allowed(placement: NativeAdPlacement, isRtbList: Boolean): Boolean =
    placement.isBrowseList && !isRtbList
}

/**
 * Emits [items] with one native ad inserted after `position` of them when [nativeAd] is loaded.
 * [itemContent] always receives the organic index of the item, unaffected by the ad, so ranks
 * and analytics positions are unchanged. Callers pass this only for supported browsing contexts;
 * Updates, RTB, bonus / reward and install-management lists never reach it.
 *
 * @param key stable item key; the ad uses its own key so a late-loading ad never reuses an app row.
 */
fun <T> LazyListScope.itemsWithNativeAd(
  items: List<T>,
  nativeAd: NativeAdUiState,
  placement: NativeAdPlacement,
  key: ((index: Int, item: T) -> Any)? = null,
  adContent: @Composable LazyItemScope.(ad: NativeAdUiState.Loaded) -> Unit,
  itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) {
  val loaded = nativeAd as? NativeAdUiState.Loaded
  val adIndex = NativeAdListPlan.adIndex(
    itemCount = items.size,
    position = loaded?.position ?: placement.defaultPosition,
    hasAd = loaded != null,
  )
  val keyFor: ((Int) -> Any)? = key?.let { k -> { index: Int -> k(index, items[index]) } }

  if (adIndex == null || loaded == null) {
    items(count = items.size, key = keyFor) { index -> itemContent(index, items[index]) }
    return
  }
  items(count = adIndex, key = keyFor) { index -> itemContent(index, items[index]) }
  item(key = NativeAdListPlan.adKey(placement), contentType = NativeAdListPlan.AD_CONTENT_TYPE) {
    adContent(loaded)
  }
  items(
    count = items.size - adIndex,
    key = keyFor?.let { k -> { offset: Int -> k(offset + adIndex) } },
  ) { offset ->
    val index = offset + adIndex
    itemContent(index, items[index])
  }
}

/**
 * Drives a list slot: active while the host is resumed, the list is long enough for the effective
 * configured position and the slot's lazy index is near the list's own live visible range
 * ([listState]); deactivated when the screen is backgrounded, the list shrinks below the position
 * or the user scrolls far past the slot. Requests carry the list size so the controller checks the
 * configured position against the rows actually shown. The target index is known from the
 * configured position even while the ad is not loaded, so an unloaded slot is handled the same as
 * a loaded one. The list state is only read, never changed.
 */
@Composable
fun NativeAdListSlotEffect(
  viewModel: NativeAdViewModel,
  itemCount: Int,
  listState: LazyListState,
) {
  val resumed = rememberHostResumed()
  val configuredPosition by viewModel.configuredPosition.collectAsState()
  val slotIndex = NativeAdListPlan.slotIndex(configuredPosition, viewModel.placement.defaultPosition)
  val nearVisible by remember(listState, slotIndex) {
    derivedStateOf {
      val visible = listState.layoutInfo.visibleItemsInfo
      NativeAdVisibility.isIndexNearVisible(
        index = slotIndex,
        firstVisible = visible.firstOrNull()?.index ?: 0,
        lastVisible = visible.lastOrNull()?.index ?: 0,
        margin = NEAR_VISIBLE_MARGIN_ITEMS,
      )
    }
  }
  val active = NativeAdListPlan.shouldBeActive(
    resumed = resumed,
    itemCount = itemCount,
    configuredPosition = configuredPosition,
    defaultPosition = viewModel.placement.defaultPosition,
    nearVisible = nearVisible,
  )
  LaunchedEffect(viewModel, active, itemCount) {
    viewModel.setSlotActive(active)
    if (active) viewModel.startLoading(listSize = itemCount)
  }
  DisposableEffect(viewModel) {
    onDispose { viewModel.setSlotActive(false) }
  }
}

/** Items on either side of the visible range that still count as near for a list slot. */
private const val NEAR_VISIBLE_MARGIN_ITEMS = 2
