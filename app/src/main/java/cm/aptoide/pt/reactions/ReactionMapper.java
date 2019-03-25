package cm.aptoide.pt.reactions;

import cm.aptoide.pt.R;

public class ReactionMapper {

  public static int mapReaction(String reactionType) {
    int reaction = R.drawable.ic_react_placeholder;
    switch (reactionType) {
      case "thumbs_up":
        reaction = R.drawable.ic_react_thumbs_up;
        break;
      case "thumbs_down":
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
