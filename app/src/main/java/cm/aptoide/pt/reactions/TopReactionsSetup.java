package cm.aptoide.pt.reactions;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.data.TopReaction;
import java.util.List;

import static cm.aptoide.pt.reactions.ReactionMapper.mapReaction;

public class TopReactionsSetup {

  private ImageView firstReaction;
  private ImageView secondReaction;
  private ImageView thirdReaction;
  private TextView numberOfReactions;

  public TopReactionsSetup() {

  }

  public void initialReactionsSetup(View view) {
    firstReaction = view.findViewById(R.id.reaction_1);
    secondReaction = view.findViewById(R.id.reaction_2);
    thirdReaction = view.findViewById(R.id.reaction_3);
    numberOfReactions = view.findViewById(R.id.number_of_reactions);
  }

  public void setReactions(List<TopReaction> reactions, int numberOfReactions, Context context) {
    ImageView[] imageViews = { firstReaction, secondReaction, thirdReaction };
    int validReactions = 0;
    for (int i = 0; i < imageViews.length; i++) {
      if (i < reactions.size() && isReactionValid(reactions.get(i)
          .getType())) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(mapReaction(reactions.get(i)
                .getType()), imageViews[i]);
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
    return mapReaction(reaction) != -1;
  }

  public void onDestroy() {
    firstReaction = null;
    secondReaction = null;
    thirdReaction = null;
    numberOfReactions = null;
  }
}
