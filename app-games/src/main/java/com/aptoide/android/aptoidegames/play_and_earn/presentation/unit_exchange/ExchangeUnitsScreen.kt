package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.backgrounds.getTapToExchangeBackground
import com.aptoide.android.aptoidegames.play_and_earn.LargeUnitsMultiplierBubble
import com.aptoide.android.aptoidegames.play_and_earn.animations.MultipleUnitExchangeAnimation
import com.aptoide.android.aptoidegames.play_and_earn.animations.SingleUnitExchangeAnimation
import com.aptoide.android.aptoidegames.play_and_earn.animations.SuccessConfettiAnimation
import com.aptoide.android.aptoidegames.play_and_earn.data.rememberAvailableUnits
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val exchangeUnitsRoute = "exchangeUnits"

fun exchangeUnitsScreen() = ScreenData.withAnalytics(
  route = exchangeUnitsRoute,
  screenAnalyticsName = "ExchangeUnits",
) { _, _, navigateBack ->

  ExchangeUnitsScreen(navigateBack)
}

@Composable
fun ExchangeUnitsScreen(
  navigateBack: () -> Unit
) {
  val (units, exchangeUnits) = rememberAvailableUnits()
  var exchanged by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Palette.Black),
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = "", iconColor = Palette.White)

    if (exchanged) {
      ExchangeSuccessView(onProceed = navigateBack)
    } else {
      if (units < 200) {
        SingleUnitExchangeView(
          onExchange = {
            exchangeUnits(200)
            exchanged = true
          }
        )
      } else {
        MultipleUnitsExchangeView(
          availableUnits = units,
          onExchange = {
            exchangeUnits(200)
            exchanged = true
          }
        )
      }
    }

  }
}

@Composable
private fun SingleUnitExchangeView(
  onExchange: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(32.dp)
  ) {
    Spacer(modifier = Modifier.weight(80f))
    SingleUnitExchangeAnimation(
      modifier = Modifier
        .padding(horizontal = 55.dp)
        .size(250.dp)
        .clickable(onClick = onExchange)
    )
    Text(
      text = "Tap the gem to exchange your units for balance",
      modifier = Modifier.padding(horizontal = 24.dp),
      style = AGTypography.Title,
      color = Palette.White,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.weight(250f))
  }
}

@Composable
private fun MultipleUnitsExchangeView(
  availableUnits: Int,
  onExchange: () -> Unit
) {
  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    Box(
      contentAlignment = Alignment.Center
    ) {
      Image(
        modifier = Modifier.fillMaxWidth(),
        imageVector = getTapToExchangeBackground(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
      )
      LargeUnitsMultiplierBubble(availableUnits = availableUnits)
    }
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
      Spacer(modifier = Modifier.weight(193f))
      MultipleUnitExchangeAnimation(
        modifier = Modifier
          .padding(horizontal = 40.dp)
          .size(280.dp)
          .clickable(onClick = onExchange)
      )
      Text(
        text = "Tap the gem to exchange your units for balance",
        modifier = Modifier.padding(horizontal = 24.dp),
        style = AGTypography.Title,
        color = Palette.White,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.weight(107f))
    }
  }
}

@Composable
private fun ExchangeSuccessView(
  onProceed: () -> Unit
) {
  var visible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    visible = true
  }

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    SuccessConfettiAnimation(
      modifier = Modifier.fillMaxSize()
    )
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
      Spacer(modifier = Modifier.weight(140f))
      Box(
        modifier = Modifier
          .size(228.dp)
          .background(Palette.Secondary, shape = CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Column {
          AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
              initialOffsetY = { fullHeight -> fullHeight / 2 },
              animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow
              )
            )
          ) {
            Text(
              text = "$2.00",
              style = AGTypography.Title,
              color = Palette.White,
              fontSize = 42.sp
            )
          }
        }
      }
      Text(
        text = "You just earned \$2.00 in Aptoide Balance to use on your games",
        modifier = Modifier.padding(horizontal = 24.dp),
        style = AGTypography.Title,
        color = Palette.White,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.weight(128f))
      //TODO: fix button
      AccentSmallButton(
        title = "Awesome", //TODO: hardcoded string
        onClick = onProceed,
        modifier = Modifier
          .padding(horizontal = 24.dp)
          .padding(bottom = 23.dp)
          .fillMaxWidth()
          .requiredHeight(48.dp)
      )
    }
  }
}

@PreviewDark
@Composable
private fun ExchangeUnitsScreenPreview() {
  ExchangeUnitsScreen(
    navigateBack = {}
  )
}
