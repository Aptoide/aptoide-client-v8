package cm.aptoide.pt.editorial;

import cm.aptoide.pt.reactions.data.ReactionType;
import java.util.List;

public class FakeReactionModel {

  private final String cardId;
  private final ReactionType userReaction;
  private final List<ReactionType> reactionTypes;
  private final String numberOfReactions;

  public FakeReactionModel(String cardId, ReactionType userReaction,
      List<ReactionType> reactionTypes, String numberOfReactions) {
    this.cardId = cardId;
    this.userReaction = userReaction;
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

  public ReactionType getUserReaction() {
    return userReaction;
  }
}
