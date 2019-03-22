package cm.aptoide.pt.reactions.network;

import cm.aptoide.pt.editorial.ReactionsResponse;
import rx.Single;

public interface ReactionsService {
  Single<ReactionsResponse> setReaction(String id, String reaction);

  Single<LoadReactionModel> loadReactionModel(String cardId);
}