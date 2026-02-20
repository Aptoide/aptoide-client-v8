package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow

/**
 * Creates and remembers a [LazyListState] that fires [onItemVisible] exactly once
 * per unique item index as items scroll into view.
 *
 * The set of already-seen indices is persisted via [rememberSaveable], so impressions
 * are not re-sent on recomposition or when navigating back to the screen.
 */
@Composable
fun rememberImpressionTrackingListState(
  onItemVisible: (Int) -> Unit = {},
): LazyListState {
  val lazyListState = rememberLazyListState()
  val impressionsSent = rememberSaveable(
    saver = listSaver(
      save = { it.toList() },
      restore = { it.toMutableSet() }
    )
  ) { mutableSetOf<Int>() }
  LaunchedEffect(lazyListState) {
    snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.map { it.index } }
      .collect { visibleIndices ->
        visibleIndices.forEach { index ->
          if (index !in impressionsSent) {
            impressionsSent.add(index)
            onItemVisible(index)
          }
        }
      }
  }
  return lazyListState
}
