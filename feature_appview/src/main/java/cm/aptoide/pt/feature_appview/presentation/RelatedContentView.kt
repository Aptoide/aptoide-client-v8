package cm.aptoide.pt.feature_appview.presentation

import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_editorial.data.ArticleType
import cm.aptoide.pt.feature_editorial.presentation.isNavigating
import cm.aptoide.pt.feature_reactions.ReactionMapper
import cm.aptoide.pt.feature_reactions.TopReactionsPreview
import cm.aptoide.pt.feature_reactions.data.TopReaction
import cm.aptoide.pt.feature_reactions.ui.ReactionsPopup
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun RelatedContentView(
  relatedContentList: List<RelatedCard>,
  listScope: LazyListScope?,
  navController: NavHostController
) {
  listScope?.item { Box(modifier = Modifier.padding(top = 24.dp)) }
  listScope?.items(relatedContentList) { relatedCard ->
    RelatedContentCard(relatedCard)
  }
}

@Composable
fun RelatedContentCard(relatedCard: RelatedCard) {
  Column(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
      .height(256.dp)
      .fillMaxWidth()
  ) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(bottom = 8.dp)) {
      Image(
        painter = rememberImagePainter(relatedCard.icon,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "Background Image",
        modifier = Modifier
          .fillMaxWidth()
          .height(168.dp)
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
          text = ArticleType.GAME_OF_THE_WEEK.label,
          style = AppTheme.typography.button_S,
          color = Color.White,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
        )
      }
    }
    Text(
      text = relatedCard.title,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.medium_M
    )
    Text(
      text = relatedCard.summary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.regular_XXS
    )
    Row(
      modifier = Modifier
        .height(32.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      val topReactionsPreview = TopReactionsPreview()
      AndroidView(
        modifier = Modifier
          .wrapContentWidth()
          .padding(end = 16.dp),
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
                  ), relatedCard.reactionsNumber + 1, view.context
                )
                if (topReactionsPreview.isReactionValid(it.name)) {
                  reactButton.setImageResource(ReactionMapper.mapReaction(it.name))
                } else {
                  reactButton.setImageResource(
                    ReactionMapper.mapReaction(
                      ReactionMapper.mapUserReaction(
                        it
                      )
                    )
                  )
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
              ), relatedCard.reactionsNumber, view.context
            )
          }
        }
      )
      Text(
        text = TextFormatter.formatDate(relatedCard.date),
        modifier = Modifier.padding(end = 16.dp),
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center,
        color = AppTheme.colors.editorialDateColor
      )
      Image(
        painter = rememberImagePainter(
          R.drawable.ic_views,
          builder = {
            placeholder(R.drawable.ic_views)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Editorial views",
        modifier = Modifier
          .padding(end = 8.dp)
          .width(14.dp)
          .height(8.dp)
      )
      Text(
        text = TextFormatter.withSuffix(relatedCard.views) + " views",
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center, color = AppTheme.colors.greyText
      )
    }
  }
}

