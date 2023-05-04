package cm.aptoide.pt.download_view.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cm.aptoide.pt.aptoide_ui.theme.AppTheme


@Composable
fun InstallSourcesDialog(
  onSettings: () -> Unit,
  onCancel: () -> Unit,
) {
  Dialog(onDismissRequest = {}, properties = DialogProperties()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(RoundedCornerShape(24.dp))
        .background(color = AppTheme.colors.background)
        .padding(all = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          text = "By default system doesn't allow Aptoide to install apps. You need to mark Aptoide as the installation source first.",
          style = AppTheme.typography.button_M,
          color = Color.Black,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
        ) {
          Button(
            onClick = onCancel,
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
          ) {
            Text(
              text = "Cancel",
              maxLines = 1,
              style = AppTheme.typography.button_M,
              color = Color.White,
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Button(
            onClick = onSettings,
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
          ) {
            Text(
              text = "Settings",
              maxLines = 1,
              style = AppTheme.typography.button_M,
              color = Color.White,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
