package cm.aptoide.pt.aptoide_ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.greyLight
import cm.aptoide.pt.aptoide_ui.theme.greyMedium
import cm.aptoide.pt.aptoide_ui.theme.orangeGradient
import cm.aptoide.pt.aptoide_ui.theme.textWhite

@Composable
fun GradientButton(
  title: String,
  modifier: Modifier,
  gradient: Brush = orangeGradient,
  isEnabled: Boolean = true,
  style: TextStyle = LocalTextStyle.current,
  onClick: () -> Unit,
) {
  Button(
    enabled = isEnabled,
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    contentPadding = PaddingValues(0.dp),
    modifier = modifier,
    colors = ButtonDefaults
      .buttonColors(backgroundColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = if (isEnabled) gradient else
            Brush.horizontalGradient(listOf(greyLight, greyLight)),
          shape = RoundedCornerShape(16.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = title,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Visible,
        maxLines = 1,
        style = style,
        color = if (isEnabled) textWhite else greyMedium,
      )
    }
  }
}
