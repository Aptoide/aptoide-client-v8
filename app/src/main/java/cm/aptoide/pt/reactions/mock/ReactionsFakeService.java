package cm.aptoide.pt.reactions.mock;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.network.ReactionsService;
import rx.Single;

public abstract class ReactionsFakeService implements ReactionsService {
  @Override public Single<ReactionsResponse> setReaction(String id, String like) {
    return Single.just(new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SUCCESS));
  }
}