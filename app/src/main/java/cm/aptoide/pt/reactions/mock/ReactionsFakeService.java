package cm.aptoide.pt.reactions.mock;

import cm.aptoide.pt.reactions.data.ReactionType;
import cm.aptoide.pt.reactions.network.ReactionsService;
import rx.Completable;

public abstract class ReactionsFakeService implements ReactionsService {
  @Override public Completable setReaction(String id, ReactionType like) {
    return Completable.complete();
  }
}