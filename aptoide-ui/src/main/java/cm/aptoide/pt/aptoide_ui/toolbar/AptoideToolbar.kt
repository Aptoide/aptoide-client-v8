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
              contentDescription = stringResource(R.string.home_overflow_talkback)
            )
          }
          DropdownMenu(
            expanded = showMenu.value,
            onDismissRequest = { showMenu.value = false }
          ) {
            DropdownMenuItem(
              onClick = {
                showMenu.value = false
                println(R.string.overflow_menu_settings)
              }
            ) {
              Text(text = stringResource(R.string.overflow_menu_settings))
            }
            DropdownMenuItem(
              onClick = {
                showMenu.value = false
                println(R.string.overflow_menu_terms_conditions)
              }
            ) {
              Text(text = stringResource(R.string.overflow_menu_terms_conditions))
            }
            DropdownMenuItem(
              onClick = {
                showMenu.value = false
                println(R.string.overflow_menu_privacy_policy)
              }
            ) {
              Text(text = stringResource(R.string.overflow_menu_privacy_policy))
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
    }
  }
}
