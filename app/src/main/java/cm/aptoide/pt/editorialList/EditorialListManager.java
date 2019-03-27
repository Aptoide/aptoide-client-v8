package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.ReactionsManager;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;
  private final ReactionsManager reactionsManager;

  public EditorialListManager(EditorialListRepository editorialListRepository,
      ReactionsManager reactionsManager) {
    this.editorialListRepository = editorialListRepository;
    this.reactionsManager = reactionsManager;
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

  public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return reactionsManager.loadReactionModel(cardId, groupId);
  }

  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reaction) {
    return reactionsManager.setReaction(cardId, groupId, reaction);
  }
}
