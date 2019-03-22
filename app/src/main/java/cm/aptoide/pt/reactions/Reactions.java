package cm.aptoide.pt.reactions;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.network.ReactionsService;
import rx.Single;

public class Reactions {

  private final ReactionsService reactionsService;

  public Reactions(ReactionsService reactionsService) {
    this.reactionsService = reactionsService;
  }

  public Single<ReactionsResponse> react(String id, String type) {
    return reactionsService.setReaction(id, type);
  }
}