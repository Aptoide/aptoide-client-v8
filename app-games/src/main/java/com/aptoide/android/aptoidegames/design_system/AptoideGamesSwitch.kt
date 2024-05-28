package com.aptoide.android.aptoidegames.design_system

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AptoideGamesSwitch(
  checked: Boolean,
  onCheckedChanged: (checked: Boolean) -> Unit,
) {
  val color by animateColorAsState(
    targetValue = if (checked) AppTheme.colors.switchOnStateColor
    else AppTheme.colors.switchOffStateColor, label = ""
  )

  var forceAnimationCheck by remember { mutableStateOf(false) }

  val currentOnCheckedChange by rememberUpdatedState(onCheckedChanged)

  val currentChecked by rememberUpdatedState(checked)

  val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

  val swipeableState = rememberSwipeableState(
    initialValue = checked
  )
  LaunchedEffect(swipeableState) {
    snapshotFlow { swipeableState.currentValue }
      .collectLatest { newValue ->
        println("currentChecked: $currentChecked")
        if (currentChecked != newValue) {
          currentOnCheckedChange(newValue)
          forceAnimationCheck = !forceAnimationCheck
        }
      }
  }

  LaunchedEffect(checked, forceAnimationCheck) {
    println("Checked: $checked")
    if (checked != swipeableState.currentValue) {
      swipeableState.animateTo(checked)
    }
  }

  val anchorRight = with(LocalDensity.current) { (40.dp - 16.dp - 3.dp).toPx() }
  val anchorLeft = with(LocalDensity.current) { (3.dp).toPx() }

  val anchors =
    mapOf(anchorLeft to false, anchorRight to true) // Maps anchor points (in px) to states


  Box(
    Modifier
      .size(40.dp)
      .toggleable(
        value = checked,
        role = Role.Switch,
        onValueChange = onCheckedChanged,
        interactionSource = interactionSource,
        indication = null
      )
  ) {
    Row(
      modifier = Modifier
        .align(Alignment.Center)
        .height(10.dp)
        .width(30.dp)
        .swipeable(
          state = swipeableState,
          anchors = anchors,
          thresholds = { _, _ -> FractionalThreshold(0.3f) },
          orientation = Orientation.Horizontal
        )
        .border(4.dp, color)
        .background(Color.Transparent),
      verticalAlignment = Alignment.CenterVertically
    ) {}

    Box(
      Modifier
        .align(Alignment.CenterStart)
        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
        .size(16.dp)
        .clip(RoundedCornerShape(50))
        .indication(
          interactionSource = interactionSource,
          indication = rememberRipple(bounded = false, radius = 90.dp)
        )
        .background(color)
    )
  }
}

@Preview
@Composable
fun CustomSwitchPreviewOff() {
  AptoideTheme {
    AptoideGamesSwitch(
      checked = false,
      onCheckedChanged = {}
    )
  }
}

@Preview
@Composable
fun CustomSwitchPreviewOn() {
  AptoideTheme {
    AptoideGamesSwitch(
      checked = true,
      onCheckedChanged = {}
    )
  }
}
