package cm.aptoide.pt.reactions;

public class ReactionEvent {

  private final String cardId;
  private final String reactionType;
  private final String groupId;

  public ReactionEvent(String cardId, String reactionType, String groupId) {

    this.cardId = cardId;
    this.reactionType = reactionType;
    this.groupId = groupId;
  }

  public String getCardId() {
    return cardId;
  }

  public String getReactionType() {
    return reactionType;
  }

  public String getGroupId() {
    return groupId;
  }
}
