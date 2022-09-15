package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.R
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun ReviewsView(app: App) {
  if ((app.rating.totalVotes == 0L)) {
    Row(modifier = Modifier.padding(start = 40.dp, top = 22.dp, end = 32.dp)) {
      Column(
        modifier = Modifier.padding(end = 43.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "-", fontSize = MaterialTheme.typography.h3.fontSize)
        RatingStars(0.0)
        Text(
          text = "" + app.rating.totalVotes + " Reviews",
          modifier = Modifier.padding(top = 12.dp)
        )
      }
      Column(modifier = Modifier.padding(top = 12.dp)) {
        app.rating.votes?.forEach {
          VotesRow(ratingNumber = it.value.toString(), progress = 0f)
        }
      }

    }
  } else {
    Row(modifier = Modifier.padding(start = 40.dp, top = 22.dp, end = 32.dp)) {
      Column(
        modifier = Modifier.padding(end = 43.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "" + app.rating.avgRating, fontSize = MaterialTheme.typography.h3.fontSize)
        RatingStars(app.rating.avgRating)
        Text(
          text = "" + app.rating.totalVotes + " Reviews",
          modifier = Modifier.padding(top = 12.dp)
        )
      }
      Column(modifier = Modifier.padding(top = 12.dp)) {
        app.rating.votes?.forEach {
          val progress = (it.count.toDouble() / app.rating.totalVotes).toFloat()
          VotesRow(ratingNumber = it.value.toString(), progress = progress)
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
        painter = rememberImagePainter(
          R.drawable.ic_icon_star,
          builder = {
            placeholder(R.drawable.ic_icon_star)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Filled rating",
        modifier = Modifier
          .padding(end = 4.dp)
          .width(12.dp)
          .height(12.dp)
      )
    }
    for (i in ratingAsInt..4) {
      Image(
        painter = rememberImagePainter(
          R.drawable.ic_icon_star_empty,
          builder = {
            placeholder(R.drawable.ic_icon_star_empty)
            transformations(RoundedCornersTransformation())
          }),
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
fun VotesRow(ratingNumber: String, progress: Float) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
    Text(
      text = ratingNumber,
      modifier = Modifier.padding(end = 10.dp),
      fontSize = MaterialTheme.typography.overline.fontSize
    )
    LinearProgressIndicator(
      modifier = Modifier
        .height(8.dp)
        .clip(RoundedCornerShape(8.dp)),
      backgroundColor = Color(0xFF4C4C4C),
      progress = progress,
      color = Color(0xffFE6446)
    )

  }
}