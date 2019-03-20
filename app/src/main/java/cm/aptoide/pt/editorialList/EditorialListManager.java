package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.editorial.FakeReactionModel;
import cm.aptoide.pt.editorial.FakeReactionsManager;
import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.data.ReactionType;
import rx.Observable;
import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;
  private final FakeReactionsManager fakeReactionsManager;

  public EditorialListManager(EditorialListRepository editorialListRepository,
      FakeReactionsManager fakeReactionsManager) {
    this.editorialListRepository = editorialListRepository;
    this.fakeReactionsManager = fakeReactionsManager;
  }

  Single<EditorialListViewModel> loadEditorialListViewModel(boolean loadMore,
      boolean invalidateCache) {
    if (loadMore) {
      return loadMoreCurationCards();
    } else {
      return editorialListRepository.loadEditorialListViewModel(invalidateCache);
    }
  }

  public boolean hasMore() {
    return editorialListRepository.hasMore();
  }

  private Single<EditorialListViewModel> loadMoreCurationCards() {
    return editorialListRepository.loadMoreCurationCards();
  }

  public Observable<FakeReactionModel> loadReactionModel(String cardId) {
    return fakeReactionsManager.loadReactionModel(cardId);
  }

  public Observable<ReactionsResponse> setReaction(String cardId, ReactionType reaction) {
    return fakeReactionsManager.setReaction(cardId, reaction);
  }
}
