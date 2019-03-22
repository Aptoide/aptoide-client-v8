package cm.aptoide.pt.editorial;

import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.home.HomeBundle;
import cm.aptoide.pt.reactions.data.ReactionType;

public class ReactionsHomeEvent extends EditorialHomeEvent {
  private final String reaction;

  public ReactionsHomeEvent(String cardId, HomeBundle bundle, int bundlePosition, Type clickType,
      String reaction) {
    super(cardId, bundle, bundlePosition, clickType);
    this.reaction = reaction;
  }

  public String getReaction() {
    return reaction;
  }
}
