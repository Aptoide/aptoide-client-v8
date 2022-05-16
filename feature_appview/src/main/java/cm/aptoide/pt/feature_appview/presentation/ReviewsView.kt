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
      Text(text = "" + app.rating, fontSize = MaterialTheme.typography.h3.fontSize)
      val ratingAsInt = app.rating
      Text(text = "" + 771 + " Reviews", modifier = Modifier.padding(top = 12.dp))
      /*Image(){

      }*/
    }
    Column (modifier= Modifier.padding(top=12.dp)) {
      //val votesList = app.rating.votes
      val totalVotes = 771

      val votes5 = (432.0 / totalVotes).toFloat()
      val votes4 = (47.0 / totalVotes).toFloat()
      val votes3 = (33.0 / totalVotes).toFloat()
      val votes2 = (27.0 / totalVotes).toFloat()
      val votes1 = (232.0 / totalVotes).toFloat()
      VotesRow("5", votes5)
      VotesRow("4", votes4)
      VotesRow("3", votes3)
      VotesRow("2", votes2)
      VotesRow("1", votes1)
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