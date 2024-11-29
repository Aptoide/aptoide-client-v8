package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .wrapContentWidth(if (isUserMessage) Alignment.End else Alignment.Start)
  ) {
    if (!isUserMessage) {
      Text(
        text = stringResource(R.string.genai_assistant_title),
        style = AGTypography.BodyBold,
        modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
      )
    } else {
      Text(
        text = stringResource(R.string.genai_me_title),
        style = AGTypography.BodyBold,
        modifier = Modifier
          .padding(end = 8.dp, bottom = 1.dp)
          .align(Alignment.End)
      )
    }
    Column(
      modifier = Modifier
        .clip(shape = RoundedCornerShape(2.dp))
        .background(
          color = if (isUserMessage) Palette.Primary else Color.Transparent,
        )
        .border(2.dp, color = if (isUserMessage) Color.Transparent else Palette.Primary)
        .padding(12.dp)
    ) {
      if (message != null) {
        Text(
          text = message.replace("\"", ""),
          style = AGTypography.Body,
          color = if (isUserMessage) Palette.Black else Palette.White,
          fontSize = 16.sp,
        )
      } else {
        Text(
          text = stringResource(R.string.genai_introduction_body),
          style = AGTypography.Body,
          color = if (isUserMessage) Palette.Black else Palette.White,
          fontSize = 16.sp,
        )
      }

      apps?.forEach { app ->
        val rememberedApp = rememberApp("package_name=$app")
        val fn = rememberedApp.second
        when (val state = rememberedApp.first) {
          AppUiState.Error -> {} //runs the reload function
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
      }
    }
  }
}
