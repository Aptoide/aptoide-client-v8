package cm.aptoide.pt.feature_reactions;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.feature_reactions.data.TopReaction;
import coil.Coil;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;
import java.util.List;

import static cm.aptoide.pt.feature_reactions.ReactionMapper.mapReaction;

public class TopReactionsPreview {

  private ImageView firstReaction;
  private ImageView secondReaction;
  private ImageView thirdReaction;
  private TextView numberOfReactions;
  private ImageView[] imageView;

  public TopReactionsPreview() {

  }

  public void initialReactionsSetup(View view) {
    firstReaction = view.findViewById(R.id.reaction_1);
    secondReaction = view.findViewById(R.id.reaction_2);
    thirdReaction = view.findViewById(R.id.reaction_3);
    numberOfReactions = view.findViewById(R.id.number_of_reactions);
    imageView = new ImageView[] { firstReaction, secondReaction, thirdReaction };
  }

  public void setReactions(List<TopReaction> reactions, int numberOfReactions, Context context) {
    ImageView[] imageViews = { firstReaction, secondReaction, thirdReaction };
    int validReactions = 0;
    for (int i = 0; i < imageViews.length; i++) {
      if (i < reactions.size() && isReactionValid(reactions.get(i)
          .getType())) {
        Coil.imageLoader(context)
            .enqueue(new ImageRequest.Builder(context).data(mapReaction(reactions.get(i)
                    .getType()))
                .crossfade(true)
                .size(16)
                .transformations(new CircleCropTransformation())
                .target(imageViews[i])
                .build());
        //loadWithShadowCircleTransform(mapReaction(reactions.get(i)
        //    .getType()), imageViews[i], context);
        imageViews[i].setVisibility(View.VISIBLE);
        validReactions++;
      } else {
        imageViews[i].setVisibility(View.GONE);
      }
    }
    if (numberOfReactions > 0 && validReactions > 0) {
      this.numberOfReactions.setText(String.valueOf(numberOfReactions));
      this.numberOfReactions.setVisibility(View.VISIBLE);
    } else {
      this.numberOfReactions.setVisibility(View.GONE);
    }
  }

  public boolean isReactionValid(String reaction) {
    return !reaction.equals("") && mapReaction(reaction) != -1;
  }

  public void clearReactions() {
    for (ImageView imageView : imageView) {
      imageView.setVisibility(View.GONE);
    }
    this.numberOfReactions.setVisibility(View.GONE);
  }

  public void onDestroy() {
    firstReaction = null;
    secondReaction = null;
    thirdReaction = null;
    numberOfReactions = null;
  }
}
