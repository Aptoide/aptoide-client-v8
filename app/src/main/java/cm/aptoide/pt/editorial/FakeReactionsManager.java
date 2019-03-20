package cm.aptoide.pt.editorial;

import cm.aptoide.pt.reactions.data.ReactionType;
import java.util.Arrays;
import rx.Observable;

import static cm.aptoide.pt.reactions.data.ReactionType.LIKE;
import static cm.aptoide.pt.reactions.data.ReactionType.THUG;
import static rx.Observable.just;

public class FakeReactionsManager {

  public Observable<FakeReactionModel> loadReactionModel(String cardId) {
    return just(new FakeReactionModel(cardId, LIKE, Arrays.asList(THUG, LIKE), "100"));
  }

  public Observable<ReactionsResponse> setReaction(String cardId, ReactionType reaction) {
    return Observable.just(
        new ReactionsResponse(ReactionsResponse.ReactionResponseMessage.SUCCESS));
  }
}
