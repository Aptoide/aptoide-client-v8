package cm.aptoide.pt.reactions;

import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import cm.aptoide.pt.reactions.network.ReactionsService;
import java.util.HashMap;
import rx.Observable;
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
    if (hasNotReacted(cardId, groupId)) {
      return reactionsService.setReaction(cardId, groupId, reactionType);
    } else {
      return reactionsService.setSecondReaction(getUID(cardId + groupId), reactionType);
    }
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
    return userReactions.get(identifier);
  }

  private boolean hasNotReacted(String cardId, String groupId) {
    return getUID(cardId + groupId) == null || getUID(cardId + groupId).equals("");
  }

  public Observable<Boolean> isFirstReaction(String cardId, String groupId) {
    return Observable.just(hasNotReacted(cardId, groupId));
  }
}
