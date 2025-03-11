package com.aptoide.android.aptoidegames.feature_promotional

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Downloading
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Error
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Install
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installed
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installing
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Migrate
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Outdated
import cm.aptoide.pt.download_view.presentation.DownloadUiState.ReadyToInstall
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun NewAppPromotionalView(
  bundle: Bundle,
  navigate: (String) -> Unit
) {
  bundle.view?.let {
    val (uiState, _) = rememberApp(it)

    when (uiState) {
      is AppUiState.Idle -> NewAppPromotional(
        app = uiState.app,
        label = bundle.title,
        navigate = navigate
      )

      is AppUiState.Loading -> LoadingView()
      is AppUiState.NoConnection,
      is AppUiState.Error -> Unit
    }
  }
}

@Composable
private fun NewAppPromotional(app: App, label: String, navigate: (String) -> Unit) {
  val downloadState = rememberDownloadState(app)
  val isDownloading = remember(downloadState) {
    isDownloading(downloadState)
  }

  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .clickable(onClick = { navigate(buildAppViewRoute(app)) })
  ) {
    AptoidePromotionalFeatureGraphicImage(
      featureGraphic = app.featureGraphic,
      label = label,
      hasAppCoins = app.isAppCoins
    )
    Row(
      modifier = Modifier.padding(top = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
      )
      Column(
        modifier = Modifier
          .padding(start = 8.dp, end = 8.dp)
          .weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = app.name,
          modifier = Modifier
            .clearAndSetSemantics {}
            .wrapContentHeight(unbounded = true),
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AGTypography.DescriptionGames
        )
        if (isDownloading) {
          ProgressText(
            modifier = Modifier.wrapContentHeight(unbounded = true, align = Alignment.Top),
            app = app,
            showVersionName = false
          )
        } else {
          AppInfo(app)
        }

      }
      InstallViewShort(app = app)
    }
  }
}

@Composable
private fun AppInfo(app: App) {
  if (app.isAppCoins) {
    Row(
      modifier = Modifier
        .wrapContentHeight(
          unbounded = true,
          align = Alignment.Top
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        imageVector = getBonusIcon(
          giftColor = Palette.Primary,
          outlineColor = Palette.Black,
        ),
        contentDescription = null,
        modifier = Modifier
          .padding(end = 8.dp)
          .size(16.dp),
      )
      Text(
        modifier = Modifier
          .padding(end = 8.dp),
        text = stringResource(R.string.promotional_now_with_appcoins),
        maxLines = 1,
        style = AGTypography.InputsXSRegular,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

fun isDownloading(downloadState: DownloadUiState?): Boolean {
  return when (downloadState) {
    is Downloading,
    is Error,
    is Installing,
    is ReadyToInstall,
    is Waiting,
    is Uninstalling -> true

    is Install,
    is Outdated,
    is Migrate,
    is DownloadUiState.MigrateAlias,
    is Installed,
    null -> false
  }
}
