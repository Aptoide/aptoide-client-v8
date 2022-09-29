package cm.aptoide.pt.feature_editorial.presentation

import android.view.LayoutInflater
import android.widget.ImageButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_editorial.data.ArticleType
import cm.aptoide.pt.feature_reactions.ReactionMapper.mapReaction
import cm.aptoide.pt.feature_reactions.ReactionMapper.mapUserReaction
import cm.aptoide.pt.feature_reactions.TopReactionsPreview
import cm.aptoide.pt.feature_reactions.data.TopReaction
import cm.aptoide.pt.feature_reactions.ui.ReactionsPopup
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

var isNavigating = false

@Composable
fun EditorialViewCard(
  articleId: String,
  title: String,
  image: String,
  subtype: ArticleType,
  summary: String,
  date: String,
  views: Long,
  reactionsNumber: Int,
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
        painter = rememberImagePainter(image,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "Background Image",
        modifier = Modifier
          .height(136.dp)
          .width(280.dp)
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
          text = subtype.label.uppercase(),
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
        .height(32.dp)
    ) {
      val topReactionsPreview = TopReactionsPreview()
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
          val view = LayoutInflater.from(context)
            .inflate(R.layout.reactions_layout, null, false)
          val reactButton: ImageButton = view.findViewById(R.id.add_reactions)
          view.apply {
            //can potentially set listeners here.
            topReactionsPreview.initialReactionsSetup(view)
            reactButton.setOnClickListener {
              val reactionsPopup = ReactionsPopup(view.context, reactButton)
              reactionsPopup.setOnReactionsItemClickListener {
                topReactionsPreview.setReactions(
                  listOf(
                    TopReaction("thumbs_up", 10),
                    TopReaction("laugh", 10),
                    TopReaction("love", 7)
                  ), reactionsNumber + 1, view.context
                )
                if (topReactionsPreview.isReactionValid(it.name)) {
                  reactButton.setImageResource(mapReaction(it.name))
                } else {
                  reactButton.setImageResource(mapReaction(mapUserReaction(it)))
                }
                reactionsPopup.dismiss()
              }

              reactionsPopup.show()
            }
          }
        },
        update = { view ->
          //bug here, this will only work once.
          if (!isNavigating) {
            topReactionsPreview.setReactions(
              listOf(
                TopReaction("thumbs_up", 10),
                TopReaction("laugh", 10),
                TopReaction("love", 7)
              ), reactionsNumber, view.context
            )
          }
        }
      )


      Text(
        text = "" + date,
        modifier = Modifier.padding(end = 16.dp),
        style = AppTheme.typography.regular_XXS
      )
      Text(
        text = "$views views",
        style = AppTheme.typography.regular_XXS
      )
    }
  }
}