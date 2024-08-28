package com.aptoide.android.aptoidegames.installer

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.installer.platform.UserActionRequest.ConfirmationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.InstallationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.PermissionAction
import cm.aptoide.pt.installer.platform.UserConfirmation
import cm.aptoide.pt.installer.presentation.UserActionViewModel
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UserActionDialog() {
  val viewModel = hiltViewModel<UserActionViewModel>()
  val state by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var isOnForeground by remember { mutableStateOf(false) }
  var installationActionLaunched by remember { mutableStateOf(false) }

  LaunchedEffect(state) {
    if (state is InstallationAction) installationActionLaunched = false
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isOnForeground = true
      } else if (event == Lifecycle.Event.ON_PAUSE) {
        isOnForeground = false
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

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
    key2 = isOnForeground,
    key3 = installationActionLaunched,
    block = {
      if (isOnForeground) {
        when (val it = state) {
          is InstallationAction -> {
            if (!installationActionLaunched) {
              intentLauncher.launch(it.intent)
              installationActionLaunched = true
            }
          }

          is PermissionAction -> permissionLauncher.launch(it.permission)
          else -> Unit
        }
      }
    }
  )

  (state as? ConfirmationAction)?.let {
    PermissionsContent(rationale = it.confirmation.getSourceString()) {
      if (it.confirmation == UserConfirmation.WRITE_EXTERNAL_RATIONALE) {
        DialogButton(
          title = stringResource(id = R.string.ok_button),
          onClick = { viewModel.onResult(false) },
        )
      } else {
        DialogButton(
          title = stringResource(id = R.string.cancel_button),
          onClick = { viewModel.onResult(false) },
        )
        DialogButton(
          title = stringResource(id = R.string.settings_title),
          onClick = { viewModel.onResult(true) },
        )
      }
    }
  }
}

@Composable
fun UserConfirmation.getSourceString(): String = stringResource(
  when (this) {
    UserConfirmation.INSTALL_SOURCE -> R.string.error_install_permissions_body
    UserConfirmation.WRITE_EXTERNAL_RATIONALE,
    UserConfirmation.WRITE_EXTERNAL,
    -> R.string.storage_access_permission_request_message
  }
)

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
        .background(color = Palette.Black)
        .padding(all = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          text = rationale,
          style = AGTypography.InputsM,
          color = Color.White,
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
private fun DialogButton(
  title: String,
  onClick: () -> Unit
) = PrimarySmallButton(
  onClick = onClick,
  title = title,
)

@PreviewDark
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
