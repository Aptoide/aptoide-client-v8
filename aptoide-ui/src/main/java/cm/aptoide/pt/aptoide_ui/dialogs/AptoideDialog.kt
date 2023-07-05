package cm.aptoide.pt.aptoide_ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cm.aptoide.pt.aptoide_ui.buttons.GradientButton
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.appCoinsButtonGradient
import cm.aptoide.pt.theme.greyLight

@Composable
fun AptoideDialog(
  title: String,
  positiveText: String = "Ok",
  isPositiveEnabled: Boolean = true,
  onPositiveClicked: () -> Unit,
  onDismissDialog: () -> Unit,
  contentComponent: @Composable () -> Unit,
) {
  Dialog(onDismissRequest = onDismissDialog) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = AppTheme.colors.background
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(horizontal = 16.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = title,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = AppTheme.typography.medium_M,
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          ) { contentComponent() }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onDismissDialog,
              elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, greyLight),
              modifier = Modifier
                .weight(1f)
                .height(40.dp),
              colors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.colors.background
              )
            ) {
              Text(
                text = "CANCEL",
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = AppTheme.typography.button_M,
                color = AppTheme.colors.primaryGrey
              )
            }

            GradientButton(
              title = positiveText.uppercase(),
              modifier = Modifier
                .weight(1f)
                .height(40.dp),
              gradient = appCoinsButtonGradient,
              isEnabled = isPositiveEnabled,
              style = AppTheme.typography.button_M,
              onClick = onPositiveClicked
            )
          }
        }
      }
    }
  }
}
