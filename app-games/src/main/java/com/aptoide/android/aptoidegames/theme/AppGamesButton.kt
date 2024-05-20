package com.aptoide.android.aptoidegames.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getRandomString
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Default
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Gray

sealed class ButtonStyle {
  abstract val fillWidth: Boolean

  data class Default(override val fillWidth: Boolean) : ButtonStyle()
  data class Gray(override val fillWidth: Boolean) : ButtonStyle()
}

@PreviewDark
@Composable
fun AppGamesButtonPreview() {
  AptoideTheme {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Default(fillWidth = false),
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Gray(fillWidth = false),
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Default(fillWidth = false),
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Gray(fillWidth = false),
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Default(fillWidth = true)
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Gray(fillWidth = true)
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Default(fillWidth = true)
      )
      AppGamesButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Gray(fillWidth = true)
      )
    }
  }
}

@PreviewDark
@Composable
fun AppGamesOutlinedButtonPreview() {
  AptoideTheme {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Default(fillWidth = false),
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Gray(fillWidth = false),
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Default(fillWidth = false),
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Gray(fillWidth = false),
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Default(fillWidth = true)
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = false,
        style = Gray(fillWidth = true)
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Default(fillWidth = true)
      )
      AppGamesOutlinedButton(
        title = getRandomString(1..2, capitalize = true),
        onClick = {},
        enabled = true,
        style = Gray(fillWidth = true)
      )
    }
  }
}

@Composable
fun AppGamesButton(
  title: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle = Default(fillWidth = false),
) {

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val color = if (isPressed) Color.Black.copy(alpha = 0.1f) else Color.Transparent

  Button(
    onClick = onClick,
    modifier = if (style.fillWidth) {
      modifier
        .height(48.dp)
        .fillMaxWidth()
    } else {
      modifier
        .height(32.dp)
        .wrapContentWidth()
    },
    enabled = enabled,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = CutCornerShape(0),
    interactionSource = interactionSource,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = when (style) {
        is Default -> color.compositeOver(AppTheme.colors.defaultButtonColor)
        is Gray -> AppTheme.colors.grayButtonColor
      },
      disabledBackgroundColor = AppTheme.colors.disabledButtonColor,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    title?.let {
      Text(
        text = it,
        maxLines = 1,
        textAlign = TextAlign.Center,
        style = if (style.fillWidth) {
          AppTheme.typography.inputs_L
        } else {
          AppTheme.typography.inputs_S
        },
        color = when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonTextColor
            is Gray -> pureWhite
          }

          else -> AppTheme.colors.disabledButtonTextColor
        }
      )
    }
  }
}

@Composable
fun AppGamesOutlinedButton(
  title: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  style: ButtonStyle = Default(fillWidth = false),
) {

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val color = if (isPressed) AppTheme.colors.primary.copy(alpha = 0.2f) else Color.Transparent

  Button(
    onClick = onClick,
    modifier = if (style.fillWidth) {
      modifier
        .height(48.dp)
        .fillMaxWidth()
    } else {
      modifier
        .height(32.dp)
        .wrapContentWidth()
    },
    enabled = enabled,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = CutCornerShape(0),
    border = BorderStroke(
      width = if (style.fillWidth) {
        3.dp
      } else {
        1.dp
      },
      brush = SolidColor(
        when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonColor
            is Gray -> AppTheme.colors.grayButtonColor
          }

          else -> AppTheme.colors.disabledButtonTextColor
        }
      )
    ),
    interactionSource = interactionSource,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = color,
      disabledBackgroundColor = Color.Transparent,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    title?.let {
      Text(
        text = it,
        maxLines = 1,
        textAlign = TextAlign.Center,
        style = if (style.fillWidth) {
          AppTheme.typography.inputs_L
        } else {
          AppTheme.typography.inputs_S
        },
        color = when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonColor
            is Gray -> AppTheme.colors.disabledButtonColor
          }

          else -> AppTheme.colors.disabledButtonTextColor
        }
      )
    }
  }
}
