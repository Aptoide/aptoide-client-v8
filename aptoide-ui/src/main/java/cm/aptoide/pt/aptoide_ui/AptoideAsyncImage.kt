package cm.aptoide.pt.aptoide_ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.Transformation

@Composable
fun AptoideAsyncImage(
  modifier: Modifier = Modifier,
  data: Any?,
  contentDescription: String?,
  transformations: Transformation? = null,
  colorFilter: ColorFilter? = null,
  placeholder: Painter? = null,
  error: Painter? = null,
  contentScale: ContentScale = ContentScale.Crop
) {
  AsyncImage(
    model = buildModel(data, transformations),
    placeholder = placeholder,
    error = error,
    contentDescription = contentDescription,
    contentScale = contentScale,
    colorFilter = colorFilter,
    modifier = modifier,
  )
}

@Composable
fun buildModel(data: Any?, transformations: Transformation?): ImageRequest {
  val builder: ImageRequest.Builder = ImageRequest.Builder(LocalContext.current)
    .data(data)
    .crossfade(600)
  transformations?.let { builder.transformations(it) }
  return builder.build()
}
