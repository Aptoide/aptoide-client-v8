package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_reactions.ui.ReactionsView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation

var isNavigating = false

@Composable
fun EditorialViewCard(
  articleId: String,
  title: String,
  image: String,
  label: String,
  summary: String,
  date: String,
  views: Long,
  navController: NavController,
) {
  Column(
    modifier = Modifier
      .height(227.dp)
      .width(280.dp)
      .clickable {
        isNavigating = true
        navController.navigate("editorial/${articleId}")
      }
  ) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(bottom = 8.dp)) {
      Image(
        painter = rememberAsyncImagePainter(
          ImageRequest.Builder(LocalContext.current).data(image).apply(block = fun ImageRequest.Builder.() {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(16f))
          }).build()
        ),
        contentDescription = "Background Image",
        modifier = Modifier
          .width(280.dp)
          .height(136.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      Card(
        elevation = 0.dp,
        modifier = Modifier
          .padding(start = 8.dp, top = 8.dp)
          .wrapContentWidth()
          .height(24.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(color = AppTheme.colors.editorialLabelColor)
      ) {
        Text(
          text = label,
          style = AppTheme.typography.button_S,
          color = Color.White,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
        )
      }
    }
    Text(
      text = title,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.medium_M
    )
    Text(
      text = summary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.regular_XXS
    )
    Row(
      modifier = Modifier
        .height(32.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      //bug here, isNavigating will only work once.
      ReactionsView(id = articleId, isNavigating = isNavigating)
      Text(
        text = TextFormatter.formatDate(date),
        modifier = Modifier.padding(end = 16.dp),
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center,
        color = AppTheme.colors.editorialDateColor
      )
      Image(
        painter = rememberAsyncImagePainter(
          ImageRequest.Builder(LocalContext.current).data(R.drawable.ic_views).apply(block = fun ImageRequest.Builder.() {
            placeholder(R.drawable.ic_views)
            transformations(RoundedCornersTransformation())
          }).build()
        ),
        contentDescription = "Editorial views",
        modifier = Modifier
          .padding(end = 8.dp)
          .width(14.dp)
          .height(8.dp)
      )
      Text(
        text = TextFormatter.withSuffix(views) + " views",
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center, color = AppTheme.colors.greyText
      )
    }
  }
}