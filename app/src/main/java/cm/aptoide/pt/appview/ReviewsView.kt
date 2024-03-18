package cm.aptoide.pt.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.theme.AppTheme

@Composable
fun ReviewsView(app: App) {

  val appRating = app.pRating

  if ((appRating.totalVotes == 0L)) {
    Column(modifier = Modifier.padding(top = 24.dp, start = 32.dp, end = 32.dp)) {
      Text(
        text = "There are no reviews or ratings yet. Be the first one!",
        fontSize = MaterialTheme.typography.caption.fontSize
      )
      Row(modifier = Modifier.padding(start = 8.dp, top = 34.dp)) {
        Column(
          modifier = Modifier.padding(end = 40.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "--",
            fontSize = MaterialTheme.typography.h3.fontSize,
            modifier = Modifier.padding(bottom = 18.dp)
          )
          RatingStars(0.0)
          Text(
            text = "${appRating.totalVotes} Reviews",
            modifier = Modifier.padding(top = 12.dp),
            fontSize = MaterialTheme.typography.caption.fontSize
          )
        }
        Column(modifier = Modifier.padding(top = 12.dp)) {
          appRating.votes?.forEach {
            VotesRow(
              ratingNumber = it.value.toString(),
              progress = 0f
            )
          }
        }
      }
    }
  } else {
    Row(modifier = Modifier.padding(start = 40.dp, top = 22.dp, end = 32.dp)) {
      Column(
        modifier = Modifier.padding(end = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = TextFormatter.formatDecimal(appRating.avgRating),
          fontSize = MaterialTheme.typography.h3.fontSize,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        RatingStars(appRating.avgRating)
        Text(
          text = "${appRating.totalVotes} Reviews",
          fontSize = MaterialTheme.typography.caption.fontSize,
          modifier = Modifier.padding(top = 12.dp)
        )
      }
      Column(modifier = Modifier.padding(top = 12.dp)) {
        appRating.votes?.forEach {
          val progress = (it.count.toDouble() / appRating.totalVotes).toFloat()
          VotesRow(
            ratingNumber = it.value.toString(),
            progress = progress
          )
        }
      }
    }
  }
}

@Composable
fun RatingStars(avgRating: Double) {
  val ratingAsInt = avgRating.toInt()
  Row {
    for (i in 1..ratingAsInt) {
      Image(
        imageVector = Icons.Filled.Star,
        colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
        contentDescription = "Filled rating",
        modifier = Modifier
          .padding(end = 4.dp)
          .width(12.dp)
          .height(12.dp)
      )
    }
    for (i in ratingAsInt..4) {
      Image(
        imageVector = Icons.Filled.Star,
        colorFilter = ColorFilter.tint(AppTheme.colors.primaryGrey),
        contentDescription = "Empty rating",
        modifier = Modifier
          .padding(end = 4.dp)
          .width(12.dp)
          .height(12.dp)
      )
    }
  }
}

@Composable
fun VotesRow(
  ratingNumber: String,
  progress: Float,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 12.dp)
  ) {
    Text(
      text = ratingNumber,
      modifier = Modifier.padding(end = 10.dp),
      fontSize = MaterialTheme.typography.overline.fontSize
    )
    LinearProgressIndicator(
      modifier = Modifier
        .height(8.dp)
        .clip(RoundedCornerShape(8.dp)),
      backgroundColor = AppTheme.colors.iconBackground,
      progress = progress,
      color = AppTheme.colors.primary
    )
  }
}
