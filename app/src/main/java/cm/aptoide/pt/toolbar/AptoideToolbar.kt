package cm.aptoide.pt.toolbar

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme
import kotlin.random.Random

@PreviewAll
@Composable
fun AptoideActionBarPreview() {
  AptoideTheme {
    RealAptoideActionBar(
      showMenu = Random.nextBoolean(),
      onMenuClick = {},
      onDropDownDismiss = {},
      onDropDownClick = {}
    ) {
      Row {
        Text(text = "Hello")
      }
    }
  }
}

@Composable
fun AptoideActionBar(
  rightContent: @Composable RowScope.() -> Unit = {},
) {
  var showMenu by remember { mutableStateOf(false) }

  val onMenuClick = { showMenu = !showMenu }
  val onDropDownClick = { showMenu = false }
  val onDropDownDismiss = { showMenu = false }

  RealAptoideActionBar(
    showMenu = showMenu,
    onMenuClick = onMenuClick,
    onDropDownDismiss = onDropDownDismiss,
    onDropDownClick = onDropDownClick,
    rightContent = rightContent
  )
}
@Composable
private fun RealAptoideActionBar(
  showMenu: Boolean,
  onMenuClick: () -> Unit,
  onDropDownDismiss: () -> Unit,
  onDropDownClick: () -> Unit,
  rightContent: @Composable (RowScope.() -> Unit) = {},
) {

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
          onClick = onMenuClick
        ) {
          Icon(
            imageVector = Icons.Filled.MoreVert,
            tint = AppTheme.colors.primaryGrey,
            contentDescription = "Options"
          )
        }
        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = onDropDownDismiss
        ) {
          DropdownMenuItem(onClick = onDropDownClick) {
            Text(text = "Settings")
          }
          DropdownMenuItem(onClick = onDropDownClick) {
            Text(text = "Terms & Conditions")
          }
          DropdownMenuItem(onClick = onDropDownClick) {
            Text(text = "Privacy Policy")
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
      }
    }
  }
}
