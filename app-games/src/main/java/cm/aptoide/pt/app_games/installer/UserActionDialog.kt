package cm.aptoide.pt.app_games.installer

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.installer.platform.UserActionRequest.ConfirmationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.InstallationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.PermissionAction
import cm.aptoide.pt.installer.platform.UserConfirmation
import cm.aptoide.pt.installer.platform.UserConfirmation.WRITE_EXTERNAL_RATIONALE
import cm.aptoide.pt.installer.presentation.UserActionViewModel

@Composable
fun UserActionDialog() {
  val viewModel = hiltViewModel<UserActionViewModel>()
  val state by viewModel.uiState.collectAsState()
  val context = LocalContext.current

  val intentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    onResult = {
      viewModel.onResult(it.resultCode == Activity.RESULT_OK)
    }
  )

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { result ->
      if (result) {
        viewModel.onResult(true)
      } else {
        val deniedOrNull = (context as? Activity)?.let { activity ->
          (state as? PermissionAction)?.run {
            // Check if rationale can help
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
              .takeIf { !it } // Turn [true] into [null] to conform with contract
          }
        }
        viewModel.onResult(deniedOrNull)
      }
    }
  )

  LaunchedEffect(
    key1 = state,
    block = {
      when (val it = state) {
        is InstallationAction -> intentLauncher.launch(it.intent)
        is PermissionAction -> permissionLauncher.launch(it.permission)
        else -> Unit
      }
    }
  )

  (state as? ConfirmationAction)?.let {
    PermissionsContent(rationale = it.confirmation.rationale) {
      if (it.confirmation == WRITE_EXTERNAL_RATIONALE) {
        DialogButton(
          title = "Ok",
          onClick = { viewModel.onResult(false) },
        )
      } else {
        DialogButton(
          title = "Cancel",
          onClick = { viewModel.onResult(false) },
        )
        DialogButton(
          title = "Settings",
          onClick = { viewModel.onResult(true) },
        )
      }
    }
  }
}

private val UserConfirmation.rationale
  get() = when (this) {
    UserConfirmation.INSTALL_SOURCE -> "By default system doesn't allow Aptoide to install apps. You need to mark Aptoide as the installation source first."
    UserConfirmation.WRITE_EXTERNAL_RATIONALE,
    UserConfirmation.WRITE_EXTERNAL,
    -> "In order to install the required game resources allow Aptoide to access external storage."
  }

@Composable
private fun PermissionsContent(
  rationale: String,
  buttons: @Composable RowScope.() -> Unit,
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
          text = rationale,
          style = AppTheme.typography.button_M,
          color = Color.Black,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          content = buttons,
        )
      }
    }
  }
}

@Composable
private fun DialogButton(title: String, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(8.dp),
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
  ) {
    Text(
      text = title,
      maxLines = 1,
      style = AppTheme.typography.button_M,
      color = Color.White,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@PreviewAll
@Composable
private fun PermissionsContentPreview() {
  AptoideTheme(isSystemInDarkTheme()) {
    PermissionsContent(
      rationale = "I need it! Just give it to me!",
      buttons = {
        DialogButton(
          title = "Cancel",
          onClick = {},
        )
        DialogButton(
          title = "Settings",
          onClick = {},
        )
      },
    )
  }
}
