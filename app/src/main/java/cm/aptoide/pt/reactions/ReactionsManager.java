package cm.aptoide.pt.reactions;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsService;
import rx.Single;

public class ReactionsManager {

  private final ReactionsService reactionsService;

  public ReactionsManager(ReactionsService reactionsService) {
    this.reactionsService = reactionsService;
  }

  public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return reactionsService.loadReactionModel(cardId, groupId);
  }

  public Single<ReactionsResponse> setReaction(String id, String reactionType) {
    return reactionsService.setReaction(id, reactionType);
  }
}
