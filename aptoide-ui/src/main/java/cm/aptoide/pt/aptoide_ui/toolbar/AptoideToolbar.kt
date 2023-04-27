package cm.aptoide.pt.aptoide_ui.toolbar

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import cm.aptoide.pt.aptoide_ui.R
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AptoideActionBar() {
  val showMenu = remember { mutableStateOf(false) }

  AptoideTheme {
    TopAppBar(
      backgroundColor = AppTheme.colors.background,
      elevation = Dp(0f)
    ) {
      Row {
        // Left Side of the ToolBar
        Spacer(modifier = Modifier.width(10.dp) )
        Image(
          modifier = Modifier.fillMaxHeight(),
          imageVector = AppTheme.icons.ToolbarLogo,
          contentDescription = "ToolbarLogo"
        )

        // Spacer to fill in between the icons
        Spacer(modifier = Modifier.weight(1f) )

        // Right Side of the ToolBar
        Column{
          IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = { showMenu.value = !showMenu.value }
          ) {
            Icon(
              imageVector = Icons.Filled.MoreVert,
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
                println("Settings")
              }
            ) {
              Text(text = "Settings")
            }
            DropdownMenuItem(
              onClick = {
                showMenu.value = false
                println("Terms & Conditions")
              }
            ) {
              Text(text = "Terms & Conditions")
            }
            DropdownMenuItem(
              onClick = {
                showMenu.value = false
                println("Privacy Policy")
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
}
