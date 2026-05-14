package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondaryOutlinedButton

private val apkfyRecommendationModifier = Modifier
  .defaultMinSize(minWidth = 95.dp, minHeight = 50.dp)

@Composable
fun InstallViewApkfyRecommendation(
  app: App,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
  cancelable: Boolean = true,
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel,
  )

  InstallViewApkfyRecommendationContent(
    installViewState = installViewState,
    cancelable = cancelable,
  )
}

@Composable
private fun InstallViewApkfyRecommendationContent(
  installViewState: InstallViewState,
  cancelable: Boolean = true,
) {
  when (val state = installViewState.uiState) {
    is DownloadUiState.Install -> PrimaryButton(
      onClick = state.install,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Migrate -> AccentButton(
      onClick = state.migrate,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.MigrateAlias -> AccentButton(
      onClick = state.migrateAlias,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Outdated -> PrimaryButton(
      onClick = state.update,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Waiting -> {
      state.action?.let {
        if (state.blocker != UNMETERED && cancelable) {
          SecondaryOutlinedButton(
            onClick = it,
            modifier = apkfyRecommendationModifier,
            title = installViewState.actionLabel,
          )
        }
      }
    }

    is DownloadUiState.Downloading -> if (cancelable) {
      SecondaryOutlinedButton(
        onClick = state.cancel,
        modifier = apkfyRecommendationModifier,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.ReadyToInstall -> if (cancelable) {
      SecondaryOutlinedButton(
        onClick = state.cancel,
        modifier = apkfyRecommendationModifier,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.Installed -> PrimaryOutlinedButton(
      onClick = state.open,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Error -> PrimaryButton(
      onClick = state.retry,
      modifier = apkfyRecommendationModifier,
      title = installViewState.actionLabel,
    )

    null,
    is DownloadUiState.Installing,
    is DownloadUiState.Uninstalling,
      -> Unit
  }
}
