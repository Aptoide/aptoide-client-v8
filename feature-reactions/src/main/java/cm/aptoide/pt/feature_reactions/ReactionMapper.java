package cm.aptoide.pt.feature_reactions;

import cm.aptoide.pt.feature_reactions.data.ReactionType;

public class ReactionMapper {

  public static int mapReaction(String reactionType) {
    int reaction = -1;
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

  public static String mapUserReaction(ReactionType type) {
    switch (type) {
      case LIKE:
        return "thumbs_up";
      case DOWN:
        return "thumbs_down";
      default:
        return type.toString()
            .toLowerCase();
    }
  }
}
