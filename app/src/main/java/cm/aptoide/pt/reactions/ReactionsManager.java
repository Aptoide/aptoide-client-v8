package cm.aptoide.pt.reactions;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsService;
import java.util.HashMap;
import rx.Single;

public class ReactionsManager {

  private final ReactionsService reactionsService;
  private HashMap<String, String> userReactions;

  public ReactionsManager(ReactionsService reactionsService,
      HashMap<String, String> userReactions) {
    this.reactionsService = reactionsService;
    this.userReactions = userReactions;
  }

  public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return reactionsService.loadReactionModel(cardId, groupId)
        .doOnSuccess(loadReactionModel -> userReactions.put(cardId + groupId,
            loadReactionModel.getUserId()));
  }

  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reactionType) {
    if (getUID(cardId + groupId).equals("")) {
      return reactionsService.setReaction(cardId, groupId, reactionType);
    } else {
      return reactionsService.setSecondReaction(getUID(cardId + groupId), reactionType);
    }
  }

  private String getUID(String identifier) {
    return userReactions.get(identifier);
  }
}
