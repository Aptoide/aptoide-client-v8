package cm.aptoide.pt.editorial;

import cm.aptoide.pt.reactions.data.ReactionType;

class ReactionEvent {

  private final String cardId;
  private final ReactionType reactionType;

  public ReactionEvent(String cardId, ReactionType reactionType) {

    this.cardId = cardId;
    this.reactionType = reactionType;
  }

  public String getCardId() {
    return cardId;
  }

  public ReactionType getReactionType() {
    return reactionType;
  }
}
