package cm.aptoide.pt.reactions;

import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import cm.aptoide.pt.reactions.network.ReactionsService;
import java.util.HashMap;
import rx.Single;

public class ReactionsManager {

  private final ReactionsService reactionsService;
  private HashMap<String, UserReaction> userReactions;

  public ReactionsManager(ReactionsService reactionsService,
      HashMap<String, UserReaction> userReactions) {
    this.reactionsService = reactionsService;
    this.userReactions = userReactions;
  }

  public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return reactionsService.loadReactionModel(cardId, groupId)
        .doOnSuccess(loadReactionModel -> {
          userReactions.put(cardId + groupId,
              new UserReaction(loadReactionModel.getUserId(), loadReactionModel.getMyReaction()));
        });
  }

  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reactionType) {
    return hasNotReacted(cardId, groupId).flatMap(reacted -> {
      if (reacted) {
        return reactionsService.setReaction(cardId, groupId, reactionType);
      } else {
        if (!isSameReaction(cardId, groupId, reactionType)) {
          return reactionsService.setSecondReaction(getUID(cardId + groupId), reactionType);
        }
        return Single.just(
            new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SAME_REACTION));
      }
    });
  }

  private boolean isSameReaction(String cardId, String groupId, String reactionType) {
    UserReaction userReaction = userReactions.get(cardId + groupId);
    return (userReaction != null && userReaction.getReaction()
        .equals(reactionType));
  }

  public Single<ReactionsResponse> deleteReaction(String cardId, String groupId) {
    return reactionsService.deleteReaction(getUID(cardId + groupId))
        .doOnSuccess(reactionsResponse -> {
          if (reactionsResponse.wasSuccess()) {
            userReactions.remove(cardId + groupId);
          }
        });
  }

  private String getUID(String identifier) {
    UserReaction userReaction = userReactions.get(identifier);
    String uid = "";
    if (userReaction != null) {
      uid = userReaction.getUserId();
    }
    return uid;
  }

  private Single<Boolean> hasNotReacted(String cardId, String groupId) {
    return Single.just(getUID(cardId + groupId) == null || getUID(cardId + groupId).equals(""));
  }

  public Single<Boolean> isFirstReaction(String cardId, String groupId) {
    return hasNotReacted(cardId, groupId);
  }
}
