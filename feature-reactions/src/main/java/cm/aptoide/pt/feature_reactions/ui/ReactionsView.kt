package cm.aptoide.pt.feature_reactions.ui

import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cm.aptoide.pt.feature_reactions.R
import cm.aptoide.pt.feature_reactions.ReactionMapper.mapReaction
import cm.aptoide.pt.feature_reactions.ReactionMapper.mapUserReaction
import cm.aptoide.pt.feature_reactions.TopReactionsPreview
import cm.aptoide.pt.feature_reactions.presentation.reactionsViewModel

@Composable
fun ReactionsView(id: String, isNavigating: Boolean) {
  val reactionsViewModel = reactionsViewModel(id = id)
  val uiState by reactionsViewModel.uiState.collectAsState()
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
              uiState.reactions.top,
              uiState.reactions.reactionsNumber + 1,
              view.context
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
      if (!isNavigating) {
        topReactionsPreview.setReactions(
          uiState.reactions.top,
          uiState.reactions.reactionsNumber,
          view.context
        )
      }
    }
  )
}
