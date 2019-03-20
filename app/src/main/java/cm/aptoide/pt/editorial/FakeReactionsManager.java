package cm.aptoide.pt.editorial;

import java.util.Arrays;
import rx.Observable;

import static cm.aptoide.pt.reactions.data.ReactionType.LIKE;
import static cm.aptoide.pt.reactions.data.ReactionType.THUG;

public class FakeReactionsManager {

  public Observable<FakeReactionModel> loadReactionModel(String cardId) {
    return Observable.just(new FakeReactionModel(cardId, Arrays.asList(THUG, LIKE), "100"));
  }
}
