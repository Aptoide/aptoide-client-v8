package cm.aptoide.pt.reactions.network;

import rx.Single;

public interface ReactionsService {
  Single<ReactionsResponse> setReaction(String id, String groupId, String reaction);

  Single<LoadReactionModel> loadReactionModel(String cardId, String groupId);

  Single<ReactionsResponse> setSecondReaction(String uid, String reaction);

  Single<ReactionsResponse> deleteReaction(String uid);
}