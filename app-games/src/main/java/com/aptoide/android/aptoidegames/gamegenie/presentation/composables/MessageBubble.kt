package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.videos.presentation.AppViewYoutubePlayer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubble(
  message: String?,
  isUserMessage: Boolean,
  videoId: String?,
  apps: List<App>? = null,
  navigateTo: (String) -> Unit = {},
) {
  val context = LocalContext.current
  val analytics = rememberGameGenieAnalytics()
  val textToRender = message?.replace("\"", "") ?: ""
  val segments = remember(textToRender) { parseStylizedText(textToRender) }
  val linkSegments = segments.filterIsInstance<TextSegment.Link>()

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

      StylizedMessage(
        message = message,
        fallbackResId = R.string.genai_introduction_body,
        onLinkClick = { url ->
          UrlActivity.open(context, url)
        },
        isUserMessage = isUserMessage
      )

      if (linkSegments.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          linkSegments.forEach { linkSegment ->
            LinkChip(
              text = linkSegment.text,
              onClick = { UrlActivity.open(context, linkSegment.url) },
              modifier = Modifier
                .padding(top = 8.dp),
            )
          }
        }
      }

      videoId?.let {
        AppViewYoutubePlayer(
          Modifier
            .padding(vertical = (8.5).dp, horizontal = 8.dp)
            .height(200.dp)
            .fillMaxWidth(),
          it,
        ) { }
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
