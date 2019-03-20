package cm.aptoide.pt.editorial;

import static cm.aptoide.pt.editorial.ReactionsResponse.ReactionResponseMessage.REACTIONS_EXCEEDED;
import static cm.aptoide.pt.editorial.ReactionsResponse.ReactionResponseMessage.SUCCESS;

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
    SUCCESS, GENERAL_ERROR, INVALID_USER_AGENT, TOKEN_NOT_VALID, REACTIONS_EXCEEDED
  }
}
