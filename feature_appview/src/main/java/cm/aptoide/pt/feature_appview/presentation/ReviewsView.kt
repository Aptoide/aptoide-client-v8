package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App

@Composable
fun ReviewsView(app: App) {
  Row {
    Column(modifier = Modifier.padding(end = 43.dp)) {
      Text(text = "" + app.rating)
      val ratingAsInt = app.rating
      Text(text = "" + 771 + " Reviews", modifier = Modifier.padding(top = 12.dp))
      /*Image(){

      }*/
    }
    Column {
      //val votesList = app.rating.votes
      val totalVotes = 771

      val votes5 = (432.0 / totalVotes).toFloat()
      val votes4 = (47.0 / totalVotes).toFloat()
      val votes3 = (33.0 / totalVotes).toFloat()
      val votes2 = (27.0 / totalVotes).toFloat()
      val votes1 = (232.0 / totalVotes).toFloat()
      VotesRow("1", votes1)
      VotesRow("2", votes2)
      VotesRow("3", votes3)
      VotesRow("4", votes4)
      VotesRow("5", votes5)
    }

  }
}

@Composable
fun VotesRow(ratingNumber: String, progress: Float) {
  Row {
    Text(text = ratingNumber, modifier = Modifier.padding(10.dp))
    LinearProgressIndicator(
      modifier = Modifier.padding(top = 5.dp),
      backgroundColor = Color(0xFF4C4C4C),
      progress = progress,
      color = Color(0xffFE6446)
    )

  }
}