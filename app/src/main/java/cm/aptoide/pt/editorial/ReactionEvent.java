package cm.aptoide.pt.editorial;

class ReactionEvent {

  private final String cardId;
  private final String reactionType;

  public ReactionEvent(String cardId, String reactionType) {

    this.cardId = cardId;
    this.reactionType = reactionType;
  }

  public String getCardId() {
    return cardId;
  }

  public String getReactionType() {
    return reactionType;
  }
}
