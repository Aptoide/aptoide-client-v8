package cm.aptoide.pt.reactions.network;

import retrofit2.Response;

import static cm.aptoide.pt.reactions.network.ReactionsResponse.ReactionResponseMessage.GENERAL_ERROR;
import static cm.aptoide.pt.reactions.network.ReactionsResponse.ReactionResponseMessage.REACTIONS_EXCEEDED;
import static cm.aptoide.pt.reactions.network.ReactionsResponse.ReactionResponseMessage.SUCCESS;

public class ReactionsResponse {

  private final ReactionResponseMessage reactionResponseMessage;

  public ReactionsResponse(ReactionResponseMessage reactionResponseMessage) {

    this.reactionResponseMessage = reactionResponseMessage;
  }

  public boolean wasSuccess() {
    return reactionResponseMessage == SUCCESS;
  }

  public boolean reactionsExceeded() {
    return reactionResponseMessage == REACTIONS_EXCEEDED;
  }

  public enum ReactionResponseMessage {
    SUCCESS, GENERAL_ERROR, REACTIONS_EXCEEDED
  }

  public static class ReactionResponseMapper {

    public static ReactionResponseMessage mapReactionResponse(Response httpResponse) {
      ReactionResponseMessage reactionResponseMessage = GENERAL_ERROR;
      switch (httpResponse.code()) {
        case 200:
          reactionResponseMessage = SUCCESS;
          break;
        case 201:
          reactionResponseMessage = SUCCESS;
          break;
        case 204:
          reactionResponseMessage = SUCCESS;
          break;
        case 429:
          reactionResponseMessage = REACTIONS_EXCEEDED;
          break;
      }
      return reactionResponseMessage;
    }
  }
}
