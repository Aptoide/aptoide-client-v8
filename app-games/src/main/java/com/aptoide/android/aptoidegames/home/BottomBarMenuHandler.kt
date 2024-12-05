package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cm.aptoide.pt.extensions.runPreviewable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object BottomBarMenuHandler {
  private val _currentRoute = MutableSharedFlow<String>(replay = 1)
  val currentRoute = _currentRoute.asSharedFlow()

  fun menuSelected(menu: BottomBarMenus) {
    _currentRoute.tryEmit(menu.route.split("?")[0])
  }
}

@Composable
fun <T : ScrollableState> rememberBottomBarMenuScrollState(
  state: T,
  route: String
): T = runPreviewable(
  preview = { state },
  real = {
    var previousRoute: String? = null
    LaunchedEffect(Unit) {
      BottomBarMenuHandler.currentRoute.collect {
        if (it == previousRoute && it == route) {
          when (state) {
            is LazyListState -> state.animateScrollToItem(0)
            is LazyGridState -> state.animateScrollToItem(0)
          }
        } else {
          previousRoute = it
        }
      }
    }
    state
  }
)
