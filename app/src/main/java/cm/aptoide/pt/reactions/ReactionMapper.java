package cm.aptoide.pt.reactions;

import cm.aptoide.pt.R;
import cm.aptoide.pt.reactions.data.ReactionType;

public class ReactionMapper {

  public static int mapReaction(String reactionType) {
    int reaction = R.drawable.ic_react_placeholder;
    switch (reactionType) {
      case "like":
        reaction = R.drawable.ic_react_thumbs_up;
        break;
      case "down":
        reaction = R.drawable.ic_react_thumbs_down;
        break;
      case "love":
        reaction = R.drawable.ic_react_love;
        break;
      case "thug":
        reaction = R.drawable.ic_react_thug;
        break;
      case "laugh":
        reaction = R.drawable.ic_react_laugh;
        break;
    }
    return reaction;
  }
}
