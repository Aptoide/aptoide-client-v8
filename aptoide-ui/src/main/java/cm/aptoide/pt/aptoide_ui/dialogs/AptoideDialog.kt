package cm.aptoide.pt.aptoide_ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cm.aptoide.pt.aptoide_ui.buttons.GradientButton
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.appCoinsButtonGradient
import cm.aptoide.pt.aptoide_ui.theme.shapes

@Composable
fun AptoideDialog(
  title: String,
  titleStyle: TextStyle = AppTheme.typography.regular_S,
  dialogWidth: Dp = 312.dp,
  dialogHeight: Dp = 200.dp,
  positiveText: String = "Ok",
  isPositiveEnabled: Boolean = true,
  onPositiveClicked: () -> Unit,
  onDismissDialog: () -> Unit,
  contentComponent: @Composable () -> Unit,
) {
  Dialog(
    onDismissRequest = onDismissDialog,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = AppTheme.colors.background
    ) {
      Box(
        modifier = Modifier
          .width(dialogWidth)
          .height(dialogHeight)
          .padding(start = 16.dp, top = 23.dp, end = 16.dp, bottom = 24.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = title,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = titleStyle,
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          ) { contentComponent() }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onDismissDialog,
              elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
              shape = shapes.large,
              border = BorderStroke(1.dp, AppTheme.colors.downloadProgressBarBackgroundColor),
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
