package com.aptoide.android.aptoidegames.search.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getRatingStar
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun SearchSponsoredBannerView(
  rtbApp: RTBApp,
  onClick: () -> Unit,
) {
  val app = rtbApp.app

  val painter = rememberAsyncImagePainter(model = app.icon)
  var dominantColor by remember { mutableStateOf(Palette.GreyDark) }
  var overlayAlpha by remember { mutableStateOf(0.3f) }

  LaunchedEffect(painter.state) {
    val state = painter.state
    if (state is AsyncImagePainter.State.Success) {
      val hwBitmap = (state.result.drawable as? BitmapDrawable)?.bitmap
      hwBitmap?.let { bmp ->
        val extracted = withContext(Dispatchers.Default) {
          val swBitmap = bmp.copy(Bitmap.Config.ARGB_8888, false)
          swBitmap?.let {
            val swatch = androidx.palette.graphics.Palette.from(it).generate()
              .dominantSwatch
            it.recycle()
            swatch?.let { s ->
              val hsl = s.hsl
              Pair(Color(s.rgb), hsl[2])
            }
          }
        }
        extracted?.let { (color, lightness) ->
          dominantColor = color
          overlayAlpha = 0.2f + (lightness * 0.2f)
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(104.dp)
      .border(width = 4.dp, color = Palette.GreyDark)
      .clickable(onClick = onClick)
  ) {
    // Background with dominant color
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(dominantColor)
    )

    // Centered app icon as background image
    Image(
      painter = painter,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.Center)
        .height(104.dp)
    )

    // Semi-transparent black overlay for text readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = overlayAlpha))
    )

    // Content area
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      // Left content: app name + rating
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        // App name
        Text(
          text = app.name,
          style = AGTypography.DescriptionGames,
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.widthIn(max = 204.dp),
        )

        // Rating row
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Image(
            imageVector = getRatingStar(Palette.White),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = String.format(Locale.US, "%.1f", app.rating.avgRating),
            style = AGTypography.InputsXS,
            color = Palette.White,
            modifier = Modifier.padding(start = 2.dp),
          )
        }
      }

      // Install button (right side)
      Box(
        modifier = Modifier
          .background(Palette.Primary)
          .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.search_sponsored_install),
          style = AGTypography.InputsS,
          color = Palette.Black,
        )
      }
    }

    // Label bar (top-left corner, overlapping border)
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .background(Palette.GreyDark)
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = stringResource(R.string.search_sponsored_suggested),
        style = AGTypography.BodyBold,
        color = Palette.GreyLight,
      )
      Text(
        text = stringResource(R.string.search_sponsored_label),
        style = AGTypography.InputsXXS,
        color = Palette.GreyLight,
      )
    }
  }
}
