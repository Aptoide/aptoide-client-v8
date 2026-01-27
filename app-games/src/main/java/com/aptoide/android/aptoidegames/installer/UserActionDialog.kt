package com.aptoide.android.aptoidegames.installer

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.extensions.toAnnotatedString
import cm.aptoide.pt.installer.platform.UserActionRequest.ConfirmationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.InstallationAction
import cm.aptoide.pt.installer.platform.UserActionRequest.PermissionAction
import cm.aptoide.pt.installer.platform.UserActionRequest.PermissionState
import cm.aptoide.pt.installer.platform.UserConfirmation
import cm.aptoide.pt.installer.presentation.UserActionViewModel
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.installer.analytics.rememberInstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.toAnalyticsPayload
import com.aptoide.android.aptoidegames.permissions.AppPermissionsViewModel
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

  val permissionsViewModel = hiltViewModel<AppPermissionsViewModel>()


  LaunchedEffect(state) {
    if (state is InstallationAction) installationActionLaunched = false
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isOnForeground = true
      } else if (event == Lifecycle.Event.ON_STOP) {
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
      viewModel.onResult(result)
    }
  )

  val installAnalytics = rememberInstallAnalytics()
  LaunchedEffect(
    key1 = state,
    key2 = isOnForeground,
    key3 = installationActionLaunched,
    block = {
      if (isOnForeground) {
        when (val it = state) {
          is InstallationAction -> {
            if (!installationActionLaunched) {
              if (it.intent.isInstallationIntent()) {
                val packageName = it.intent
                  .getStringExtra("${BuildConfig.APPLICATION_ID}.pn") ?: "NaN"
                val analyticsPayload = it.intent
                  .getStringExtra("${BuildConfig.APPLICATION_ID}.ap").toAnalyticsPayload()
                installAnalytics.sendInstallDialogImpressionEvent(packageName, analyticsPayload)
              }
              intentLauncher.launch(it.intent)
              installationActionLaunched = true
            }
          }

          is PermissionAction -> permissionLauncher.launch(it.permission)

          is PermissionState -> {
            val requestedPermission = permissionsViewModel.hasRequestedPermission(it.permission)

            val shouldShowRationale = (context as? Activity)?.let { activity ->
              ActivityCompat.shouldShowRequestPermissionRationale(activity, it.permission)
            } ?: false

            if (!requestedPermission && !shouldShowRationale) {
              viewModel.onResult(true)
            } else if (shouldShowRationale) {
              viewModel.onResult(null)
              permissionsViewModel.setPermissionRequested(it.permission)
            } else {
              viewModel.onResult(false)
            }
          }

          else -> Unit
        }
      }
    }
  )

  (state as? ConfirmationAction)?.let {
    PermissionsDialog(rationale = it.confirmation.getSourceString()) {
      PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { viewModel.onResult(true) },
        title = it.confirmation.getPositiveButtonTitle()
      )
      PrimaryTextButton(
        onClick = { viewModel.onResult(false) },
        text = it.confirmation.getNegativeButtonTitle()
      )
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
fun UserConfirmation.getPositiveButtonTitle(): String = stringResource(
  when (this) {
    UserConfirmation.INSTALL_SOURCE -> R.string.settings_title
    UserConfirmation.WRITE_EXTERNAL_RATIONALE,
    UserConfirmation.WRITE_EXTERNAL,
      -> R.string.ok_button
  }
)

@Composable
fun UserConfirmation.getNegativeButtonTitle(): String = stringResource(
  when (this) {
    UserConfirmation.INSTALL_SOURCE,
    UserConfirmation.WRITE_EXTERNAL_RATIONALE,
    UserConfirmation.WRITE_EXTERNAL,
      -> R.string.cancel_button
  }
)

@Composable
fun PermissionsDialog(
  rationale: String,
  buttons: @Composable ColumnScope.() -> Unit,
) {
  val annotatedText = rationale.toAnnotatedString(AGTypography.SubHeadingM.toSpanStyle())

  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .padding(vertical = 24.dp, horizontal = 16.dp)
        .width(328.dp)
        .wrapContentHeight()
        .background(color = Palette.GreyDark),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .padding(top = 32.dp)
          .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Image(
          imageVector = getAptoideGamesToolbarLogo(Palette.Primary),
          contentDescription = null
        )
        Text(
          text = annotatedText,
          color = Palette.White,
          textAlign = TextAlign.Center,
          style = AGTypography.SubHeadingS
        )
        Column(
          modifier = Modifier.padding(bottom = 8.dp),
          content = buttons
        )
      }
    }
  }
}

@PreviewDark
@Composable
fun PermissionsDialogPreview() {
  AptoideTheme(isSystemInDarkTheme()) {
    PermissionsDialog(rationale = getRandomString(10..25)) {
      PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        title = getRandomString(1..1, capitalize = true)
      )
      PrimaryTextButton(
        onClick = {},
        text = getRandomString(1..1, capitalize = true)
      )
    }
  }
}

//System action, we cannot access it any other way
fun Intent.isInstallationIntent() =
  action == "android.content.pm.action.CONFIRM_INSTALL" || action == "android.intent.action.INSTALL_PACKAGE"
