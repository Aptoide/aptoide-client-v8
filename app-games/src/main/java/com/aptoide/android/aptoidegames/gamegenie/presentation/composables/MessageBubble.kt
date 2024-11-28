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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import com.aptoide.android.aptoidegames.appview.LoadingView
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieMessage
import com.aptoide.android.aptoidegames.gamegenie.domain.isUserMessage
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun MessageBubble(
  message: GameGenieMessage,
  apps: List<String>? = null,
  navigateTo: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .wrapContentWidth(if (message.isUserMessage()) Alignment.End else Alignment.Start)
  ) {
    if (!message.isUserMessage()) {
      Text(
        text = "Assistant", //TODO take this out
        style = AGTypography.BodyBold,
        modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
      )
    } else {
      Text(
        text = "Me", //TODO take this out
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
          color = if (message.isUserMessage()) Palette.Primary else Color.Transparent,
        )
        .border(2.dp, color = if (message.isUserMessage()) Color.Transparent else Palette.Primary)
        .padding(12.dp)
    ) {
      Text(
        text = message.messageBody.replace("\"", ""),
        style = AGTypography.Body,
        color = if (message.isUserMessage()) Palette.Black else Palette.White,
        fontSize = 16.sp,
      )

      apps?.forEach { app ->
        val rememberedApp = rememberApp("package_name=$app")
        val fn = rememberedApp.second
        when (val state = rememberedApp.first) {
          AppUiState.Error -> GenericErrorView(fn)//runs the reload function
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