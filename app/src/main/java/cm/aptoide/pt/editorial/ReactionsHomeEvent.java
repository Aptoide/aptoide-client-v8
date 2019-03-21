package cm.aptoide.pt.editorial;

import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.home.HomeBundle;
import cm.aptoide.pt.reactions.data.ReactionType;

public class ReactionsHomeEvent extends EditorialHomeEvent {
  private final ReactionType reaction;

  public ReactionsHomeEvent(String cardId, HomeBundle bundle, int bundlePosition, Type clickType,
      ReactionType reaction) {
    super(cardId, bundle, bundlePosition, clickType);
    this.reaction = reaction;
  }

  public ReactionType getReaction() {
    return reaction;
  }
}
