package cm.aptoide.pt.app_games.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.ButtonStyle.Default
import cm.aptoide.pt.app_games.theme.ButtonStyle.Gray
import cm.aptoide.pt.app_games.theme.ButtonStyle.Red
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.getRandomString

sealed class ButtonStyle {
  abstract val fillWidth: Boolean

  data class Default(override val fillWidth: Boolean) : ButtonStyle()
  data class Red(override val fillWidth: Boolean) : ButtonStyle()
  data class Gray(override val fillWidth: Boolean) : ButtonStyle()
}

@PreviewAll
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
        style = Red(fillWidth = false),
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
        style = Red(fillWidth = false),
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
        style = Red(fillWidth = true)
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
        style = Red(fillWidth = true)
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

@PreviewAll
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
        style = Red(fillWidth = false),
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
        style = Red(fillWidth = false),
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
        style = Red(fillWidth = true)
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
        style = Red(fillWidth = true)
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
    shape = RoundedCornerShape(50),
    colors = ButtonDefaults.buttonColors(
      backgroundColor = when (style) {
        is Default -> AppTheme.colors.defaultButtonColor
        is Gray -> AppTheme.colors.grayButtonColor
        is Red -> AppTheme.colors.redButtonColor
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
          AppTheme.typography.buttonTextLight
        } else {
          AppTheme.typography.buttonTextMedium
        },
        color = when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonTextColor
            is Gray -> AppTheme.colors.grayButtonTextColor
            is Red -> AppTheme.colors.redButtonTextColor
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
    shape = RoundedCornerShape(50),
    border = BorderStroke(
      width = 1.dp,
      brush = SolidColor(
        when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonColor
            is Gray -> AppTheme.colors.grayButtonColor
            is Red -> AppTheme.colors.redButtonColor
          }

          else -> AppTheme.colors.disabledButtonTextColor
        }
      )
    ),
    colors = ButtonDefaults.buttonColors(
      backgroundColor = Color.Transparent,
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
          AppTheme.typography.buttonTextLight
        } else {
          AppTheme.typography.buttonTextMedium
        },
        color = when {
          enabled -> when (style) {
            is Default -> AppTheme.colors.defaultButtonColor
            is Gray -> AppTheme.colors.grayButtonTextColor
            is Red -> AppTheme.colors.redButtonColor
          }

          else -> AppTheme.colors.disabledButtonTextColor
        }
      )
    }
  }
}
