package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode.Restart
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Downloading
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installing
import cm.aptoide.pt.download_view.presentation.DownloadUiState.ReadyToInstall
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.drawables.icons.getRectProgress
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppIconWProgress(
  app: App,
  contentDescription: String?,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = Modifier.clearAndSetSemantics {
      this.contentDescription = contentDescription ?: ""
    }
  ) {
    val state = rememberDownloadState(app = app)
    val animation = remember { Animatable(0f) }
    val transform by remember(state) {
      derivedStateOf {
        when (state) {
          is Waiting,
          is Downloading,
          is ReadyToInstall,
          is Installing,
          is Uninstalling,
          -> GreyscaleTransformation()

          else -> null
        }
      }
    }
    LaunchedEffect(key1 = state?.javaClass?.simpleName) {
      when (state) {
        is Waiting,
        is ReadyToInstall,
        is Installing,
        is Uninstalling,
        -> {
          animation.snapTo(0f)
          animation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
              animation = tween(1000, easing = LinearEasing),
              repeatMode = Restart
            )
          )
        }

        else -> animation.stop()
      }
    }

    AppIconImage(
      modifier = modifier,
      data = app.icon,
      contentDescription = contentDescription,
      transformation = transform,
    )
    when (state) {
      is Waiting,
      is ReadyToInstall,
      is Installing,
      is Uninstalling,
      -> getRectProgress(
        color = Palette.Primary,
        progress = 0.25f,
        progressOffset = animation.value
      )

      is Downloading -> getRectProgress(
        color = Palette.Primary,
        progress = state.downloadProgress / 100f
      )

      else -> null
    }?.let {
      Image(
        modifier = modifier,
        imageVector = it,
        contentDescription = null,
        contentScale = ContentScale.Crop
      )
    }
  }
}
