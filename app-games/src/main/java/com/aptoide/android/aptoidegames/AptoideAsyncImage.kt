package com.aptoide.android.aptoidegames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.aptoide.android.aptoidegames.theme.AppTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.Transformation

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
) {

  val placeholderColor = AppTheme.colors.dividerColor

  AsyncImage(
    model = buildModel(data, transformations),
    placeholder = if (placeholder) remember { ColorPainter(placeholderColor) } else null,
    error = remember { ColorPainter(placeholderColor) },
    contentDescription = contentDescription,
    contentScale = ContentScale.Crop,
    colorFilter = colorFilter,
    modifier = modifier,
  )
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
