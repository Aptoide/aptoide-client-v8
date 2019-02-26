package cm.aptoide.pt.editorialList;

import java.util.List;
import rx.Single;

public class EditorialListRepository {

  private final EditorialListService editorialListService;
  private EditorialListViewModel cachedEditorialListViewModel;

  public EditorialListRepository(EditorialListService editorialListService) {
    this.editorialListService = editorialListService;
  }

  public Single<EditorialListViewModel> loadEditorialListViewModel() {
    if (cachedEditorialListViewModel != null) {
      return Single.just(cachedEditorialListViewModel);
    }
    return loadNewEditorialListViewModel(0);
  }

  private Single<EditorialListViewModel> loadNewEditorialListViewModel(int offset) {
    return editorialListService.loadEditorialListViewModel(offset)
        .map(editorialListViewModel -> {
          if (!editorialListViewModel.hasError() && !editorialListViewModel.isLoading()) {
            updateCache(editorialListViewModel, false);
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
    return loadNewEditorialListViewModel(cachedEditorialListViewModel.getOffset()).doOnSuccess(
        editorialListViewModel -> updateCache(editorialListViewModel, true));
  }

  private void updateCache(EditorialListViewModel editorialListViewModel, boolean loadMore) {
    if (loadMore) {
      List<CurationCard> curationCards = cachedEditorialListViewModel.getCurationCards();
      curationCards.addAll(editorialListViewModel.getCurationCards());
      cachedEditorialListViewModel =
          new EditorialListViewModel(curationCards, cachedEditorialListViewModel.getOffset(),
              cachedEditorialListViewModel.getTotal());
    } else {
      cachedEditorialListViewModel = editorialListViewModel;
    }
  }
}
