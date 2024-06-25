package com.aptoide.android.aptoidegames.analytics.presentation

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cm.aptoide.pt.extensions.runPreviewable

@Composable
fun SwipeListener(
  interactionSource: InteractionSource,
) {
  runPreviewable(
    preview = { }
  ) {
    RealSwipeListener(interactionSource)
  }
}

@Composable
fun RealSwipeListener(
  interactionSource: InteractionSource,
) {
  val genericAnalytics = rememberGenericAnalytics()
  val (numberOfSwipes, setNumberOfSwipes) = remember { mutableStateOf(0) }

  val analyticsContext = AnalyticsContext.current
  val interactions by interactionSource.collectIsDraggedAsState()

  LaunchedEffect(interactions) {
    if (interactions) {
      val newNumberOfSwipes = numberOfSwipes + 1
      setNumberOfSwipes(newNumberOfSwipes)
      genericAnalytics.sendCarouselSwipe(newNumberOfSwipes, analyticsContext)
    }
  }
}
