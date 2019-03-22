package cm.aptoide.pt.reactions;

import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsRemoteService;
import rx.Single;

public class ReactionsManager {

  private final ReactionsRemoteService reactionsRemoteService;

  public ReactionsManager(ReactionsRemoteService reactionsRemoteService) {
    this.reactionsRemoteService = reactionsRemoteService;
  }

  public Single<LoadReactionModel> loadReactionModel(String cardId) {
    return reactionsRemoteService.loadReactionModel(cardId);
  }
}
