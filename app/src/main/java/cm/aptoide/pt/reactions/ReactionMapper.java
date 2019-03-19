package cm.aptoide.pt.reactions;

import cm.aptoide.pt.R;
import cm.aptoide.pt.reactions.data.ReactionType;

public class ReactionMapper {

  public static int mapReaction(ReactionType reactionType) {
    int reaction = R.drawable.ic_react_placeholder;
    switch (reactionType) {
      case LIKE:
        reaction = R.drawable.ic_react_thumbs_up;
        break;
      case DOWN:
        reaction = R.drawable.ic_react_thumbs_down;
        break;
      case LOVE:
        reaction = R.drawable.ic_react_love;
        break;
      case THUG:
        reaction = R.drawable.ic_react_thug;
        break;
      case LAUGH:
        reaction = R.drawable.ic_react_laugh;
        break;
    }
    return reaction;
  }
}
