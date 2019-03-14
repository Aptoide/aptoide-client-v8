package cm.aptoide.pt.reactions;

import cm.aptoide.pt.reactions.data.ReactionType;
import cm.aptoide.pt.reactions.network.ReactionsService;
import rx.Completable;

public class Reactions {

  private final ReactionsService reactionsService;

  public Reactions(ReactionsService reactionsService) {
    this.reactionsService = reactionsService;
  }

  public Completable react(String id, ReactionType type) {
    return reactionsService.setReaction(id, type);
  }
}