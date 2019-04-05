package cm.aptoide.pt.reactions;

public class ReactionEvent {

  private final String cardId;
  private final String reactionType;
  private final String groupId;
  private final Type type;

  public ReactionEvent(String cardId, String reactionType, String groupId, Type type) {

    this.cardId = cardId;
    this.reactionType = reactionType;
    this.groupId = groupId;
    this.type = type;
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

  public Type getType() {
    return type;
  }

  public enum Type {
    REACT, DELETE
  }
}
