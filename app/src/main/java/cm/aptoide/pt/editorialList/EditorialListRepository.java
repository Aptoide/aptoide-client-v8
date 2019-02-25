package cm.aptoide.pt.editorialList;

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
            cachedEditorialListViewModel = editorialListViewModel;
          }
          return editorialListViewModel;
        });
  }

  public boolean hasMore() {
    return cachedEditorialListViewModel.getOffset() < cachedEditorialListViewModel.getTotal();
  }

  public Single<EditorialListViewModel> loadMoreCurationCards() {
    return loadNewEditorialListViewModel(cachedEditorialListViewModel.getOffset());
  }
}
