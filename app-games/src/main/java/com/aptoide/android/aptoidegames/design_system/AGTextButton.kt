package com.aptoide.android.aptoidegames.design_system

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.getRandomString
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
private fun AGTextButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  enabled: Boolean,
  text: String,
  color: Color,
  textStyle: TextStyle,
  textDecoration: TextDecoration? = null,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val overlayColor = if (isPressed) Palette.Black.copy(alpha = 0.1f) else Color.Transparent

  TextButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier,
  ) {
    Text(
      text = text,
      style = textStyle,
      maxLines = 1,
      color = if (enabled) overlayColor.compositeOver(color) else Palette.Grey,
      textDecoration = textDecoration
    )
  }
}

@Composable
fun PrimaryTextButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  enabled: Boolean = true,
  color: Color = Palette.Primary,
  text: String,
) = AGTextButton(
  modifier = modifier
    .fillMaxWidth()
    .height(48.dp),
  onClick = onClick,
  enabled = enabled,
  text = text,
  color = color,
  textStyle = AGTypography.InputsM,
)

@Composable
fun SecondaryUnderlinedTextButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  enabled: Boolean = true,
  text: String,
) = AGTextButton(
  modifier = modifier
    .fillMaxWidth()
    .height(48.dp),
  onClick = onClick,
  enabled = enabled,
  text = text,
  color = Palette.Black,
  textStyle = AGTypography.InputsM,
  textDecoration = TextDecoration.Underline
)

@Preview(
  showSystemUi = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
  showSystemUi = true,
  uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AGTextButtonPreview() {
  AptoideTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      PrimaryTextButton(
        onClick = {},
        enabled = true,
        text = getRandomString(1..2, capitalize = true),
      )
      PrimaryTextButton(
        onClick = {},
        enabled = false,
        text = getRandomString(1..2, capitalize = true),
      )
      SecondaryUnderlinedTextButton(
        onClick = {},
        enabled = true,
        text = getRandomString(1..2, capitalize = true),
      )
      SecondaryUnderlinedTextButton(
        onClick = {},
        enabled = false,
        text = getRandomString(1..2, capitalize = true),
      )
    }
  }
}
