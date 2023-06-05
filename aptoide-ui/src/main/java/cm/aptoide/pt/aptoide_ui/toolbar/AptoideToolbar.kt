package cm.aptoide.pt.aptoide_ui.toolbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import timber.log.Timber

@Preview
@Composable
fun AptoideActionBar(
  rightContent: @Composable RowScope.() -> Unit = {},
) {
  val showMenu = remember { mutableStateOf(false) }

  TopAppBar(
    backgroundColor = AppTheme.colors.background,
    elevation = Dp(0f)
  ) {
    Row {
      // Left Side of the ToolBar
      Spacer(modifier = Modifier.width(10.dp))
      Image(
        modifier = Modifier.fillMaxHeight(),
        imageVector = AppTheme.icons.ToolbarLogo,
        contentDescription = "ToolbarLogo"
      )

      // Spacer to fill in between the icons
      Spacer(modifier = Modifier.weight(1f))

      // Right Side of the ToolBar
      rightContent()
      Column {
        IconButton(
          modifier = Modifier
              .fillMaxHeight()
              .wrapContentWidth(),
          onClick = { showMenu.value = !showMenu.value }
        ) {
          Icon(
            imageVector = Icons.Filled.MoreVert,
            tint = AppTheme.colors.primaryGrey,
            contentDescription = "Options"
          )
        }
        DropdownMenu(
          expanded = showMenu.value,
          onDismissRequest = { showMenu.value = false }
        ) {
          DropdownMenuItem(
            onClick = {
              showMenu.value = false
              Timber.d("Settings")
            }
          ) {
            Text(text = "Settings")
          }
          DropdownMenuItem(
            onClick = {
              showMenu.value = false
              Timber.d("Terms & Conditions")
            }
          ) {
            Text(text = "Terms & Conditions")
          }
          DropdownMenuItem(
            onClick = {
              showMenu.value = false
              Timber.d("Privacy Policy")
            }
          ) {
            Text(text = "Privacy Policy")
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
      }
    }
  }
}
