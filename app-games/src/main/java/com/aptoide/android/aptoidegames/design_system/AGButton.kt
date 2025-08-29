package com.aptoide.android.aptoidegames.design_system

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.getRandomString
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Preview(
  showSystemUi = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AGButtonPreview() {
  AptoideTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimaryButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondaryButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        SecondaryButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryOutlinedButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimaryOutlinedButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimaryButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondaryButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        SecondaryButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimaryOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
    }
  }
}

@Preview(
  showSystemUi = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AGSmallButtonPreview() {
  AptoideTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimarySmallButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimarySmallButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimarySmallOutlinedButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimarySmallOutlinedButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondarySmallOutlinedButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        SecondarySmallOutlinedButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TertiarySmallButton(
          onClick = {},
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        TertiarySmallButton(
          onClick = {},
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimarySmallButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimarySmallButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimarySmallOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        PrimarySmallOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondarySmallOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        SecondarySmallOutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TertiarySmallButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(0.5f),
          enabled = false,
          title = getRandomString(1..2, capitalize = true),
        )
        TertiarySmallButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth(),
          enabled = true,
          title = getRandomString(1..2, capitalize = true),
        )
      }
    }
  }
}

@Composable fun PrimaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Primary,
  title = title,
  textStyle = AGTypography.InputsL.copy(color = Palette.Black)
)

@Composable
fun PrimaryOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGOutlinedButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Primary,
  strokeWidth = 3.dp,
  title = title,
  textStyle = AGTypography.InputsL.copy(color = Palette.Primary)
)

@Composable
fun SecondaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Grey,
  title = title,
  textStyle = AGTypography.InputsL.copy(color = Palette.White)
)

@Composable
fun SecondaryOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
  textStyle: TextStyle = AGTypography.InputsL.copy(color = Palette.White),
) = AGOutlinedButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Grey,
  strokeWidth = 3.dp,
  title = title,
  textStyle = textStyle
)

@Composable fun AccentButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Secondary,
  title = title,
  textStyle = AGTypography.InputsL.copy(color = Palette.White)
)

@Composable fun PrimarySmallButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Primary,
  title = title,
  textStyle = AGTypography.InputsS.copy(color = Palette.Black)
)

@Composable fun AccentSmallButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Secondary,
  title = title,
  textStyle = AGTypography.InputsS.copy(color = Palette.White)
)

@Composable
fun TertiarySmallButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Grey,
  title = title,
  textStyle = AGTypography.InputsS.copy(color = Palette.Black)
)

@Composable
fun PrimarySmallOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGOutlinedButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Primary,
  strokeWidth = 1.dp,
  title = title,
  textStyle = AGTypography.InputsS.copy(color = Palette.Primary)
)

@Composable
fun SecondarySmallOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  title: String?,
) = AGOutlinedButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Grey,
  strokeWidth = 1.dp,
  title = title,
  textStyle = AGTypography.InputsS.copy(color = Palette.GreyLight)
)

@Composable
fun AGButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean,
  color: Color,
  title: String?,
  textStyle: TextStyle,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val overlayColor = if (isPressed) Palette.Black.copy(alpha = 0.1f) else Color.Transparent

  Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = CutCornerShape(0),
    interactionSource = interactionSource,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = overlayColor.compositeOver(color),
      disabledBackgroundColor = Palette.Grey,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    title?.let {
      Text(
        text = it,
        maxLines = 1,
        textAlign = TextAlign.Center,
        style = textStyle.copy(
          color = when {
            enabled -> textStyle.color
            else -> Palette.Black
          }
        ),
      )
    }
  }
}

@Composable
private fun AGOutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean,
  color: Color,
  strokeWidth: Dp,
  title: String?,
  textStyle: TextStyle,
) {

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val overlayColor = if (isPressed) color.copy(alpha = 0.2f) else Color.Transparent

  Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = CutCornerShape(0),
    border = BorderStroke(
      width = strokeWidth,
      brush = SolidColor(
        when {
          enabled -> color
          else -> Color.Transparent
        }
      )
    ),
    interactionSource = interactionSource,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = overlayColor,
      disabledBackgroundColor = Palette.Grey,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    title?.let {
      Text(
        text = it,
        maxLines = 1,
        textAlign = TextAlign.Center,
        style = textStyle.copy(
          color = when {
            enabled -> textStyle.color
            else -> Palette.Black
          },
        ),
      )
    }
  }
}

@Composable
fun AGContentButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean,
  color: Color,
  content: @Composable RowScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val overlayColor = if (isPressed) Palette.Black.copy(alpha = 0.1f) else Color.Transparent

  Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = CutCornerShape(0),
    interactionSource = interactionSource,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = overlayColor.compositeOver(color),
      disabledBackgroundColor = Palette.Grey,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp),
    content = content
  )
}

@Composable fun PrimaryContentButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable RowScope.() -> Unit,
) = AGContentButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Primary,
  content = content
)
