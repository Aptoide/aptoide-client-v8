package cm.aptoide.pt.editorial;

import cm.aptoide.pt.reactions.data.ReactionType;
import java.util.List;

public class FakeReactionModel {

  private final String cardId;
  private final List<ReactionType> reactionTypes;
  private final String numberOfReactions;

  public FakeReactionModel(String cardId, List<ReactionType> reactionTypes,
      String numberOfReactions) {
    this.cardId = cardId;
    this.reactionTypes = reactionTypes;
    this.numberOfReactions = numberOfReactions;
  }

  public List<ReactionType> getReactionTypes() {
    return reactionTypes;
  }

  public String getNumberOfReactions() {
    return numberOfReactions;
  }

  public String getCardId() {
    return cardId;
  }
}
