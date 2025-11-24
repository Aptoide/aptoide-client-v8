package com.aptoide.android.aptoidegames.play_and_earn.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Velocity
import androidx.navigation.NavHostController
import cm.aptoide.pt.campaigns.presentation.PaEBundlesUiState
import cm.aptoide.pt.campaigns.presentation.rememberPaEBundles
import com.aptoide.android.aptoidegames.bottom_bar.AppGamesBottomBar
import com.aptoide.android.aptoidegames.home.gamesRoute
import com.aptoide.android.aptoidegames.home.navigateTo
import com.aptoide.android.aptoidegames.play_and_earn.rememberShouldShowPlayAndEarn
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaEHomeLayout(
  navController: NavHostController,
  content: @Composable () -> Unit
) {
  val bundle = rememberPaEBundles()
    .let { it as? PaEBundlesUiState.Idle }
    ?.bundles?.trending
    ?.takeIf { it.apps.isNotEmpty() }

  val shouldShowPlayAndEarn = rememberShouldShowPlayAndEarn()
  val (hasShownHeader, markHeaderAsShown) = rememberPaEHeaderState()

  if (bundle != null && shouldShowPlayAndEarn && hasShownHeader == false) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var canScroll by remember { mutableStateOf(true) }
    var isVisible by remember { mutableStateOf(false) }
    var layoutHeightPx: Int? = remember { null }

    val checkScrollSnap = suspend {
      if (!scrollState.isScrollInProgress && canScroll) {
        layoutHeightPx?.let {
          if (scrollState.value > (layoutHeightPx!! / 2)) {
            scrollState.animateScrollTo(layoutHeightPx!!)
          } else {
            scrollState.animateScrollTo(0)
          }
        }
      }
    }

    val currentRoute by
    navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    val nestedScrollConnection = remember {
      object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
          val delta = available.y

          return if (canScroll) {
            coroutineScope.launch { scrollState.scrollBy(-delta) }
            Offset(0f, delta)
          } else {
            Offset.Zero
          }
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
          checkScrollSnap()
          return super.onPostFling(consumed, available)
        }
      }
    }

    LaunchedEffect(currentRoute) {
      val currentRoute = currentRoute?.destination?.route
      if (currentRoute != null && !currentRoute.contains(gamesRoute)) {
        isVisible = false
        canScroll = false
      }
    }

    LaunchedEffect(Unit) {
      delay(1000L)
      isVisible = true
    }

    LaunchedEffect(scrollState.value) {
      if (scrollState.value == layoutHeightPx) {
        canScroll = false

        // Mark the header bundle as shown when user scrolls it away
        markHeaderAsShown()
      }
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
      checkScrollSnap()
    }

    BoxWithConstraints(
      modifier = Modifier.fillMaxSize()
    ) {
      val maxHeight = this.maxHeight

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(
            enabled = canScroll,
            state = scrollState,
          )
          .nestedScroll(nestedScrollConnection)
      ) {
        AnimatedVisibility(
          visible = canScroll && isVisible,
          enter = fadeIn(animationSpec = tween(400)) + expandVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 60f),
            expandFrom = Alignment.Top
          ),
        ) {
          PaEHeaderBundle(
            bundle = bundle,
            modifier = Modifier
              .onGloballyPositioned {
                layoutHeightPx = it.size.height
              }
              .graphicsLayer {
                translationY = scrollState.value.toFloat()
              },
            navigate = navController::navigateTo
          )
        }

        Box(modifier = Modifier.height(maxHeight)) {
          content()
        }
      }

      if (canScroll) {
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(Palette.Black)
        ) {
          AppGamesBottomBar(navController = navController)
        }
      }
    }
  } else {
    content()
  }
}
