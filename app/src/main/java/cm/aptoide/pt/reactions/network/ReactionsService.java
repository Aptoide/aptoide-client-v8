package cm.aptoide.pt.reactions.network;

import cm.aptoide.pt.reactions.data.ReactionType;
import rx.Completable;
import rx.Single;

public interface ReactionsService {
  Completable setReaction(String id, ReactionType like);

  Single<LoadReactionModel> loadReactionModel(String cardId);
}