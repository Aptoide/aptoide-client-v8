package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.math.abs

@Composable
fun GameGenieOverlay(
  showMenu: Boolean,
  isAptoideGamesInForeground: Boolean,
  targetAppIcon: ImageBitmap?,
  isCaptureReady: Boolean,
  onMenuToggle: () -> Unit,
  onDrag: (dx: Int, dy: Int) -> Unit,
  onDragEnd: () -> Unit,
  onScreenshotRequest: () -> Unit = {},
  onMenuClosed: () -> Unit = {},
  analytics: GameGenieAnalytics,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .offset(x = 0.dp)
        .size(56.dp)
    ) {
      GameGenieIconFab(
        isAptoideGamesInForeground = isAptoideGamesInForeground,
        targetAppIcon = targetAppIcon,
        isCaptureReady = isCaptureReady,
        modifier = Modifier
          .pointerInput(Unit) {
            var totalDragDistance = 0f
            detectDragGestures(
              onDragStart = {
                totalDragDistance = 0f
              },
              onDrag = { change, dragAmount ->
                change.consume()
                totalDragDistance += abs(dragAmount.x) + abs(dragAmount.y)
                onDrag(dragAmount.x.toInt(), dragAmount.y.toInt())
              },
              onDragEnd = {
                if (totalDragDistance > 5f) {
                  onDragEnd()
                }
              }
            )
          },
        onClick = {
          if (isCaptureReady) {
            analytics.sendGameGenieOverlayClick()
            onScreenshotRequest()
          }
        },
        onLongClick = { 
          if (isCaptureReady) {
            onMenuToggle()
          }
        }
      )
    }

    if (showMenu) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable(
            onClick = { onMenuClosed() },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
          )
      )
    }
  }
}

@Composable
fun GameGenieMenu(
  onScreenshot: () -> Unit,
  onCloseOverlay: () -> Unit,
  showOnlyRemove: Boolean,
) {
  Column(
    horizontalAlignment = Alignment.End
  ) {
    if (!showOnlyRemove) {
      Surface(
        shape = RectangleShape,
        color = Palette.Black,
        modifier = Modifier
      ) {
        GameGenieMenuItem(
          iconId = R.drawable.ask_what_im_seeing,
          text = stringResource(R.string.gamegenie_overlay_ask_screenshot),
          textColor = Palette.Primary,
          onClick = onScreenshot,
          modifier = Modifier
            .width(240.dp)
            .heightIn(48.dp)
        )
      }
    }
    Surface(
      shape = RectangleShape,
      color = Palette.Black,
      modifier = Modifier
    ) {
      GameGenieMenuItem(
        iconId = null,
        text = stringResource(R.string.gamegenie_overlay_remove),
        textColor = Palette.Error,
        onClick = onCloseOverlay,
        modifier = Modifier
          .width(138.dp)
          .heightIn(32.dp)
      )
    }
  }
}

@Composable
private fun GameGenieMenuItem(
  iconId: Int?,
  text: String,
  textColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .combinedClickable(onClick = onClick)
      .padding(start = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (iconId != null) {
      Image(
        painterResource(iconId),
        contentDescription = text,
        modifier = Modifier.size(24.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      style = AGTypography.InputsM,
      color = textColor,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun GameGenieIconFab(
  modifier: Modifier = Modifier,
  isAptoideGamesInForeground: Boolean,
  targetAppIcon: ImageBitmap? = null,
  isCaptureReady: Boolean = false,
  onClick: () -> Unit = {},
  onLongClick: () -> Unit = {},
) {
  Box(
    modifier = modifier.size(56.dp),
    contentAlignment = Alignment.Center
  ) {
    val iconSize = if (isAptoideGamesInForeground) 48.dp else 40.dp
    val alpha = if (isCaptureReady) 1f else 0.5f
    val iconModifier = Modifier
      .size(iconSize)
      .graphicsLayer {
        shadowElevation = 4.dp.toPx()
        shape = CircleShape
        this.alpha = alpha
        val shadowOffset = if (isAptoideGamesInForeground) - 4.dp.toPx() else 0f
        translationY = shadowOffset
        translationX = shadowOffset
      }
      .combinedClickable(
        enabled = isCaptureReady,
        onClick = onClick,
        onLongClick = onLongClick
      )


    if (isAptoideGamesInForeground) {
      Box(
        modifier = iconModifier,
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(
            R.drawable.aptoide_games_fab_foreground
          ),
          contentDescription = "Aptoide Games Overlay Icon",
          modifier = Modifier.fillMaxSize()
        )
        if (targetAppIcon != null) {
          Image(
            bitmap = targetAppIcon,
            contentDescription = "Target App Icon",
            modifier = Modifier
              .size(20.dp)
              .align(Alignment.BottomEnd)
          )
        }
      }
    } else {
      Image(
        painter = painterResource(
          R.drawable.app_icon
        ),
        contentDescription = "Aptoide Games Overlay Icon",
        modifier = iconModifier
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun GameGenieIconFabForegroundPreview() {
  GameGenieIconFab(
    isAptoideGamesInForeground = true,
    targetAppIcon = ImageBitmap(20, 20),
    isCaptureReady = true
  )
}

@Preview(showBackground = true)
@Composable
private fun GameGenieIconFabBackgroundPreview() {
  GameGenieIconFab(
    isAptoideGamesInForeground = false,
    isCaptureReady = true
  )
}
