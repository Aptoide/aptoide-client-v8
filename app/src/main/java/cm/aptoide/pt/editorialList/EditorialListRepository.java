package cm.aptoide.pt.editorialList;

import java.util.List;
import rx.Single;

public class EditorialListRepository {

  private final EditorialListService editorialListService;
  private EditorialListViewModel cachedEditorialListViewModel;

  public EditorialListRepository(EditorialListService editorialListService) {
    this.editorialListService = editorialListService;
  }

  public Single<EditorialListViewModel> loadEditorialListViewModel(boolean invalidateCache) {
    if (cachedEditorialListViewModel != null && !invalidateCache) {
      return Single.just(cachedEditorialListViewModel);
    }
    return loadNewEditorialListViewModel(0, false);
  }

  private Single<EditorialListViewModel> loadNewEditorialListViewModel(int offset,
      boolean loadMore) {
    return editorialListService.loadEditorialListViewModel(offset)
        .map(editorialListViewModel -> {
          if (!editorialListViewModel.hasError() && !editorialListViewModel.isLoading()) {
            updateCache(editorialListViewModel, loadMore);
          }
          return editorialListViewModel;
        });
  }

  public boolean hasMore() {
    if (cachedEditorialListViewModel != null) {
      return cachedEditorialListViewModel.getOffset() < cachedEditorialListViewModel.getTotal();
    }
    return false;
  }

  public Single<EditorialListViewModel> loadMoreCurationCards() {
    int offset = 0;
    if (cachedEditorialListViewModel != null) {
      offset = cachedEditorialListViewModel.getOffset();
    }
    return loadNewEditorialListViewModel(offset, true);
  }

  private void updateCache(EditorialListViewModel editorialListViewModel, boolean loadMore) {
    if (!loadMore) {
      cachedEditorialListViewModel = editorialListViewModel;
    } else {
      List<CurationCard> curationCards = cachedEditorialListViewModel.getCurationCards();
      curationCards.addAll(editorialListViewModel.getCurationCards());
      cachedEditorialListViewModel =
          new EditorialListViewModel(curationCards, editorialListViewModel.getOffset(),
              editorialListViewModel.getTotal());
    }
  }
}
