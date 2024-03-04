package cm.aptoide.pt.editorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_reactions.ui.ReactionsView

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
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .height(227.dp)
      .width(280.dp)
      .clickable {
        isNavigating = true
        navigate(buildEditorialRoute(articleId))
      }
  ) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(bottom = 8.dp)) {
      EditorialImage(image = image, modifier = Modifier
        .width(280.dp)
        .height(136.dp)
        .clip(RoundedCornerShape(16.dp)))
      EditorialTypeLabel(label = label)
    }
    EditorialTitle(title = title, modifier = Modifier.align(Alignment.Start))
    EditorialSummary(summary = summary, modifier = Modifier.align(Alignment.Start))
    Row(
      modifier = Modifier
        .height(32.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      //bug here, isNavigating will only work once.
      ReactionsView(id = articleId, isNavigating = isNavigating)
      EditorialDate(date = date)
      EditorialViewsIcon()
      EditorialViewsText(views = views)
    }
  }
}

@Composable
fun RelatedEditorialViewCard(
  articleMeta: ArticleMeta,
  onRelatedContentClick: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
      .height(256.dp)
      .fillMaxWidth()
      .clickable { onRelatedContentClick(articleMeta.id) }
  ) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(bottom = 8.dp)) {
      EditorialImage(image = articleMeta.image, modifier = Modifier
        .fillMaxWidth()
        .height(168.dp)
        .clip(RoundedCornerShape(16.dp)))
      EditorialTypeLabel(label = articleMeta.caption.uppercase())
    }
    EditorialTitle(title = articleMeta.title, modifier = Modifier.align(Alignment.Start))
    EditorialSummary(summary = articleMeta.summary, modifier = Modifier.align(Alignment.Start))
    Row(
      modifier = Modifier
        .height(32.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      //bug here, isNavigating will only work once.
      ReactionsView(id = articleMeta.id, isNavigating = isNavigating)
      EditorialDate(date = articleMeta.date)
      EditorialViewsIcon()
      EditorialViewsText(views = articleMeta.views)
    }
  }
}

@Composable
fun EditorialImage(image: String, modifier: Modifier) = AptoideAsyncImage(
  data = image,
  contentDescription = "Background Image",
  placeholder = ColorPainter(AppTheme.colors.placeholderColor),
  modifier = modifier
)

@Composable
fun EditorialTypeLabel(label: String) = Card(
  elevation = 0.dp,
  backgroundColor = AppTheme.colors.editorialLabelColor,
  modifier = Modifier
    .padding(start = 8.dp, top = 8.dp)
    .wrapContentWidth()
    .height(24.dp)
    .clip(RoundedCornerShape(16.dp))
) {
  Text(
    text = label,
    style = AppTheme.typography.button_S,
    color = Color.White,
    textAlign = TextAlign.Center,
    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
  )
}

@Composable
fun EditorialTitle(title: String, modifier: Modifier) = Text(
  text = title,
  maxLines = 1,
  overflow = TextOverflow.Ellipsis,
  modifier = modifier,
  style = AppTheme.typography.medium_M
)

@Composable
fun EditorialSummary(summary: String, modifier: Modifier) = Text(
  text = summary,
  maxLines = 2,
  overflow = TextOverflow.Ellipsis,
  modifier = modifier,
  style = AppTheme.typography.regular_XXS
)

@Composable
fun EditorialDate(date: String) = Text(
  text = TextFormatter.formatDateToSystemLocale(LocalContext.current, date),
  modifier = Modifier.padding(end = 16.dp),
  style = AppTheme.typography.regular_XXS,
  textAlign = TextAlign.Center,
  color = AppTheme.colors.editorialDateColor
)

@Composable
fun EditorialViewsIcon() = Image(
  imageVector = AppTheme.icons.ViewsIcon,
  contentDescription = "Editorial views",
  modifier = Modifier
    .padding(end = 8.dp)
    .width(14.dp)
    .height(8.dp)
)

@Composable
fun EditorialViewsText(views: Long) = Text(
  text = TextFormatter.withSuffix(views) + " views",
  style = AppTheme.typography.regular_XXS,
  textAlign = TextAlign.Center, color = AppTheme.colors.greyText
)


