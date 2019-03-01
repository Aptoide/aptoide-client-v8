package cm.aptoide.pt.editorialList;

import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;

  public EditorialListManager(EditorialListRepository editorialListRepository) {
    this.editorialListRepository = editorialListRepository;
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
}
