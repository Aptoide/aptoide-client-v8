package com.aptoide.android.aptoidegames

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cm.aptoide.pt.extensions.runPreviewable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.Transformation
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.theme.Palette
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun AptoideFeatureGraphicImage(
  modifier: Modifier = Modifier,
  data: String?,
  contentDescription: String?,
  transformations: Transformation? = null,
) {
  val finalUrl = data?.let {
    "$data?w=512&h=250"
  }
  AptoideAsyncImage(
    modifier = modifier,
    data = finalUrl,
    contentDescription = contentDescription,
    transformations = transformations
  )
}

@Composable
fun AppIconImage(
  modifier: Modifier = Modifier,
  data: String?,
  contentDescription: String?,
  transformation: Transformation? = null,
  colorFilter: ColorFilter? = null,
) {
  val finalUrl = data?.let {
    "$data?w=256&h=256"
  }
  AptoideAsyncImage(
    modifier = modifier,
    data = finalUrl,
    contentDescription = contentDescription,
    transformations = transformation,
    colorFilter = colorFilter,
  )
}

@Composable
fun AptoideAsyncImage(
  modifier: Modifier = Modifier,
  data: Any?,
  placeholder: Boolean = true,
  contentDescription: String?,
  transformations: Transformation? = null,
  colorFilter: ColorFilter? = null,
  contentScale: ContentScale = ContentScale.Crop,
) = runPreviewable(
  preview = {
    Box(
      modifier = modifier.background(brush = generateRandomGradient()),
    )
  }
) {
  val placeholderColor = Palette.Grey

  AsyncImage(
    model = buildModel(data, transformations),
    placeholder = if (placeholder) remember { ColorPainter(placeholderColor) } else null,
    error = remember { ColorPainter(placeholderColor) },
    contentDescription = contentDescription,
    contentScale = contentScale,
    colorFilter = colorFilter,
    modifier = modifier,
  )
}

@Composable
fun AptoideAsyncImageWithFullscreen(
  modifier: Modifier = Modifier,
  data: Any?,
  placeholder: Boolean = true,
  contentDescription: String?,
  transformations: Transformation? = null,
  colorFilter: ColorFilter? = null,
  images: List<Any?> = listOf(data),
  selectedIndex: Int = 1,
  contentScale: ContentScale = ContentScale.Crop,
) {
  var expanded by remember { mutableStateOf(false) }
  AptoideAsyncImage(
    modifier = modifier.clickable { expanded = true },
    data = data,
    placeholder = placeholder,
    transformations = transformations,
    colorFilter = colorFilter,
    contentDescription = contentDescription,
    contentScale = contentScale
  )

  if (expanded) {
    FullscreenImageViewer(
      images = images,
      initialPage = selectedIndex,
      contentDescription = contentDescription,
      onDismiss = {
        expanded = false
      }
    )
  }
}

@Composable
fun FullscreenImageViewer(
  images: List<Any?>,
  initialPage: Int,
  contentDescription: String?,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      modifier = Modifier
        .background(Palette.Black)
        .fillMaxSize()
        .clipToBounds()
    ) {
      val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { images.size }
      )
      val zoomState = rememberZoomState(
        maxScale = 5f
      )

      LaunchedEffect(pagerState.currentPage) {
        zoomState.reset()
      }

      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
      ) { page ->
        AptoideAsyncImage(
          modifier = Modifier
            .fillMaxSize()
            .zoomable(zoomState),
          data = images[page],
          contentDescription = contentDescription,
          contentScale = ContentScale.Fit
        )
      }

      Image(
        imageVector = getLeftArrow(Palette.Primary, Palette.Black),
        contentDescription = stringResource(id = R.string.button_back_title),
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .align(Alignment.TopStart)
          .clickable(onClick = onDismiss)
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .size(32.dp)
      )
    }
  }
}

@Composable
fun buildModel(
  data: Any?,
  transformations: Transformation?,
): ImageRequest {
  val builder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current)
    .data(data)
    .crossfade(600)
  transformations?.let { builder.transformations(it) }
  return builder.build()
}

fun generateRandomGradient(): Brush {
  val colors = List(Random.nextInt(3..5)) {
    Color(
      red = Random.nextFloat(),
      green = Random.nextFloat(),
      blue = Random.nextFloat(),
      alpha = 1f
    )
  }

  val gradientBuilders = listOf<(List<Color>) -> Brush>(
    { colors -> Brush.verticalGradient(colors) },
    { colors -> Brush.radialGradient(colors) },
    { colors -> Brush.linearGradient(colors) },
    { colors -> Brush.sweepGradient(colors) },
    { colors -> Brush.horizontalGradient(colors) },
  )

  return gradientBuilders.random().invoke(colors)
}
