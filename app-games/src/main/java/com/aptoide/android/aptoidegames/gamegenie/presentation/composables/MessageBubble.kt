package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.toAnnotatedString
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieYoutubePlayer
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun MessageBubble(
  message: String?,
  isUserMessage: Boolean,
  videoId: String?,
  apps: List<App>? = null,
  navigateTo: (String) -> Unit = {},
  playerCache: MutableMap<String, YouTubePlayerView>,
  onHeightMeasured: ((Int) -> Unit)? = null,
  isCompanion: Boolean = false,
  gameName: String = "",
) {
  val context = LocalContext.current
  val analytics = rememberGameGenieAnalytics()

  Box(
    modifier = Modifier
      .let { base ->
        if (onHeightMeasured != null) {
          base.onGloballyPositioned { coords -> onHeightMeasured(coords.size.height) }
        } else base
      }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
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

        if (isCompanion) {
          val originalString =
            stringResource(id = R.string.gamegenie_introduction_companion_section, gameName)
          val annotatedString = originalString.toAnnotatedString(
            SpanStyle(fontWeight = AGTypography.ChatBold.fontWeight)
          )

          Text(
            text = annotatedString,
            style = AGTypography.Chat,
          )
        } else {
          StylizedMessage(
            message = message,
            fallbackResId = R.string.genai_introduction_body,
            onLinkClick = { url ->
              UrlActivity.open(context, url)
            },
            isUserMessage = isUserMessage
          )
        }

        videoId?.let { id ->
          key(id) {
            GameGenieYoutubePlayer(
              Modifier
                .padding(vertical = (8.5).dp, horizontal = 8.dp)
                .height(200.dp)
                .fillMaxWidth(),
              id,
              playerCache
            )
          }
        }

        apps?.forEachIndexed { index, app ->
          AppItem(
            app = app,
            onClick = {
              navigateTo(
                buildAppViewRoute(app)
              )
              analytics.sendGameGenieAppClick(app.packageName, index)
            },
          ) {
            InstallViewShort(app)
          }
        }
      }
    }
  }
}
