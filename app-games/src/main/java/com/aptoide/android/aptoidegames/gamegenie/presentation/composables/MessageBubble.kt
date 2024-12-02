package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.LoadingView
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun MessageBubble(
  message: String?,
  isUserMessage: Boolean,
  apps: List<String>? = null,
  navigateTo: (String) -> Unit = {},
  onAllAppsFail: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .wrapContentWidth(if (isUserMessage) Alignment.End else Alignment.Start)
  ) {
    if (!isUserMessage) {
      ChatParticipantName(
        stringResource(R.string.genai_bottom_navigation_gamegenie_button)
      )
    } else {
      ChatParticipantName(
        stringResource(R.string.genai_me_title),
        Modifier.align(Alignment.End)
      )
    }
    Column(
      modifier = Modifier
        .background(
          color = if (isUserMessage) Palette.Primary else Color.Transparent,
        )
        .border(2.dp, color = Palette.Primary)
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      if (message != null) {
        Text(
          text = message.replace("\"", ""),
          style = AGTypography.Body,
          color = if (isUserMessage) Palette.Black else Palette.White,
        )
      } else {
        Text(
          text = stringResource(R.string.genai_introduction_body),
          style = AGTypography.Body,
          color = if (isUserMessage) Palette.Black else Palette.White,
        )
      }

      val processedApps = apps?.map { app ->
        val rememberedApp = rememberApp("package_name=$app")
        val fn = rememberedApp.second
        val state = rememberedApp.first
        when (state) {
          AppUiState.Error -> {}
          is AppUiState.Idle -> {
            val fullApp = state.app
            AppItem(
              app = fullApp,
              onClick = {
                navigateTo(
                  buildAppViewRoute(fullApp)
                )
              },
            ) {
              InstallViewShort(fullApp)
            }
          }

          AppUiState.Loading -> LoadingView()
          AppUiState.NoConnection -> NoConnectionView(fn)
        }
        state
      }

      processedApps?.let {
        if (processedApps.isNotEmpty() && processedApps.all { state -> state == AppUiState.Error || state == AppUiState.NoConnection }) {
          // not sure if I should be changing the outer state, but since its controlled via interface it should be ok
          onAllAppsFail()
        }
      }
    }
  }
}
