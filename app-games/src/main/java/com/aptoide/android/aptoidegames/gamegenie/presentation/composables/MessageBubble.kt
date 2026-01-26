package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
  image: String? = null,
) {
  val context = LocalContext.current
  val analytics = rememberGameGenieAnalytics()

  val decodedBitmap = produceState<Bitmap?>(
    initialValue = null,
    key1 = image,
    key2 = isUserMessage
  ) {
    if (isUserMessage && image != null) {
      value = withContext(Dispatchers.IO) {
        try {
          decodeBitmapWithDownsampling(image, maxWidth = 800, maxHeight = 800)
        } catch (_: Exception) {
          null
        }
      }
    } else {
      value = null
    }
  }.value

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
          stringResource(R.string.game_genie_chat_name)
        )
      } else {
        ChatParticipantName(
          stringResource(R.string.genai_me_title),
          Modifier.align(Alignment.End)
        )
      }

      if (isUserMessage && decodedBitmap != null) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(
            modifier = Modifier
              .background(color = Palette.Primary)
              .border(2.dp, color = Palette.Primary)
              .padding(12.dp)
              .align(Alignment.End)
          ) {
            Image(
              bitmap = decodedBitmap.asImageBitmap(),
              contentDescription = "Uploaded image",
              modifier = Modifier
                .height(124.dp)
                .wrapContentWidth(),
              contentScale = ContentScale.FillHeight
            )
          }

          Column(
            modifier = Modifier
              .background(color = Palette.Primary)
              .border(2.dp, color = Palette.Primary)
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
                isUserMessage = true
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
      } else {
        Column(
          modifier = Modifier
            .background(
              color = if (isUserMessage) Palette.Primary else Palette.GreyDark,
            )
            .fillMaxWidth()
            .padding(16.dp)
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
}

private fun decodeBitmapWithDownsampling(
  imageSource: String,
  maxWidth: Int,
  maxHeight: Int,
): Bitmap? {
  return try {
    val file = java.io.File(imageSource)

    if (file.exists()) {
      decodeBitmapFromFile(file.absolutePath, maxWidth, maxHeight)
    } else {
      val imageBytes = Base64.decode(imageSource, Base64.DEFAULT)
      decodeBitmapFromByteArray(imageBytes, maxWidth, maxHeight)
    }
  } catch (e: Exception) {
    null
  }
}

private fun decodeBitmapFromFile(
  filePath: String,
  maxWidth: Int,
  maxHeight: Int,
): Bitmap? {
  val options = BitmapFactory.Options().apply {
    inJustDecodeBounds = true
  }
  BitmapFactory.decodeFile(filePath, options)

  options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

  options.inJustDecodeBounds = false
  return BitmapFactory.decodeFile(filePath, options)
}

private fun decodeBitmapFromByteArray(
  byteArray: ByteArray,
  maxWidth: Int,
  maxHeight: Int,
): Bitmap? {
  val options = BitmapFactory.Options().apply {
    inJustDecodeBounds = true
  }
  BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

  options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

  options.inJustDecodeBounds = false
  return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}

private fun calculateInSampleSize(
  options: BitmapFactory.Options,
  reqWidth: Int,
  reqHeight: Int,
): Int {
  val (width, height) = options.outWidth to options.outHeight
  var inSampleSize = 1

  if (height > reqHeight || width > reqWidth) {
    val halfHeight = height / 2
    val halfWidth = width / 2

    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
      inSampleSize *= 2
    }
  }

  return inSampleSize
}
