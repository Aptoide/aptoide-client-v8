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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.aptoide.android.aptoidegames.BuildConfig
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
  val initialDominantColor = Palette.GreyDark
  var dominantColor by remember { mutableStateOf(initialDominantColor) }
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

  if (BuildConfig.FLAVOR_brand == "vanilla") {
    VanillaSponsoredBanner(
      appName = app.name,
      rating = app.rating.avgRating,
      painter = painter,
      dominantColor = dominantColor,
      overlayAlpha = overlayAlpha,
      onClick = onClick,
    )
  } else {
    AGSponsoredBanner(
      appName = app.name,
      rating = app.rating.avgRating,
      painter = painter,
      dominantColor = dominantColor,
      overlayAlpha = overlayAlpha,
      onClick = onClick,
    )
  }
}

@Composable
private fun AGSponsoredBanner(
  appName: String,
  rating: Double,
  painter: AsyncImagePainter,
  dominantColor: Color,
  overlayAlpha: Float,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(104.dp)
      .border(width = 4.dp, color = Palette.GreyDark)
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(dominantColor)
    )

    Image(
      painter = painter,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.Center)
        .height(104.dp)
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = overlayAlpha))
    )

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = appName,
          style = AGTypography.DescriptionGames,
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.widthIn(max = 204.dp),
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Image(
            imageVector = getRatingStar(Palette.White),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = String.format(Locale.US, "%.1f", rating),
            style = AGTypography.InputsXS,
            color = Palette.White,
            modifier = Modifier.padding(start = 2.dp),
          )
        }
      }

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

@Composable
private fun VanillaSponsoredBanner(
  appName: String,
  rating: Double,
  painter: AsyncImagePainter,
  dominantColor: Color,
  overlayAlpha: Float,
  onClick: () -> Unit,
) {
  val outerShape = RoundedCornerShape(16.dp)
  val innerShape = RoundedCornerShape(12.dp)
  val labelShape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(outerShape)
      .background(Palette.Primary)
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 6.dp, end = 6.dp, top = 32.dp, bottom = 6.dp)
        .height(96.dp)
        .clip(innerShape)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(dominantColor)
      )

      Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .fillMaxHeight()
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = overlayAlpha))
      )

      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = appName,
            style = AGTypography.DescriptionGames,
            color = Palette.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 204.dp),
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Image(
              imageVector = getRatingStar(Palette.White),
              contentDescription = null,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = String.format(Locale.US, "%.1f", rating),
              style = AGTypography.InputsXS,
              color = Palette.White,
              modifier = Modifier.padding(start = 2.dp),
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Palette.Primary)
            .padding(horizontal = 16.dp, vertical = 9.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = stringResource(R.string.search_sponsored_install),
            style = AGTypography.InputsS,
            color = Color(0xFF1E1E26),
          )
        }
      }
    }

    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .clip(labelShape)
        .background(Palette.SecondaryLight)
        .padding(horizontal = 12.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = stringResource(R.string.search_sponsored_suggested),
        style = AGTypography.BodyBold,
        color = Palette.White,
      )
      Text(
        text = stringResource(R.string.search_sponsored_label),
        style = AGTypography.InputsXXS,
        color = Palette.Grey,
      )
    }
  }
}

