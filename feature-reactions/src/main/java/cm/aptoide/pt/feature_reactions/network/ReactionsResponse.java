package cm.aptoide.pt.feature_reactions.network;

import retrofit2.Response;

public class ReactionsResponse {

  private final ReactionResponseMessage reactionResponseMessage;

  public ReactionsResponse(ReactionResponseMessage reactionResponseMessage) {

    this.reactionResponseMessage = reactionResponseMessage;
  }

  public boolean wasSuccess() {
    return reactionResponseMessage == ReactionResponseMessage.SUCCESS;
  }

  public boolean reactionsExceeded() {
    return reactionResponseMessage == ReactionResponseMessage.REACTIONS_EXCEEDED;
  }

  public boolean differentReaction() {
    return reactionResponseMessage != ReactionResponseMessage.SAME_REACTION;
  }

  public boolean wasGeneralError() {
    return reactionResponseMessage == ReactionResponseMessage.GENERAL_ERROR;
  }

  public boolean wasNetworkError() {
    return reactionResponseMessage == ReactionResponseMessage.NETWORK_ERROR;
  }

  public enum ReactionResponseMessage {
    SUCCESS, GENERAL_ERROR, REACTIONS_EXCEEDED, SAME_REACTION, NETWORK_ERROR
  }

  public static class ReactionResponseMapper {

    public static ReactionResponseMessage mapReactionResponse(Response httpResponse) {
      ReactionResponseMessage reactionResponseMessage = ReactionResponseMessage.GENERAL_ERROR;
      switch (httpResponse.code()) {
        case 200:
        case 201:
        case 204:
          reactionResponseMessage = ReactionResponseMessage.SUCCESS;
          break;
        case 429:
          reactionResponseMessage = ReactionResponseMessage.REACTIONS_EXCEEDED;
          break;
        case 400:
        case 401:
        case 403:
        case 405:
        case 406:
        case 500:
          reactionResponseMessage = ReactionResponseMessage.GENERAL_ERROR;
          break;
      }
      return reactionResponseMessage;
    }
  }
}
