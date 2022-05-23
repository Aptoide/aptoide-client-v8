package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun ReviewsView(app: App) {
  Row {
    Column(modifier = Modifier.padding(end = 43.dp)) {
      Text(text = "" + app.rating.avgRating, fontSize = MaterialTheme.typography.h3.fontSize)
      Text(text = "" + app.rating.totalVotes + " Reviews", modifier = Modifier.padding(top = 12.dp))
      /*Image(){

      }*/
    }
    Column(modifier = Modifier.padding(top = 12.dp)) {
      app.rating.votes?.forEach {
        val progress = (it.count.toDouble() / app.rating.totalVotes).toFloat()
        VotesRow(ratingNumber = it.value.toString(), progress = progress)
      }
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