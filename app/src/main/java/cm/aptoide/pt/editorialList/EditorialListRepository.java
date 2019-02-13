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
    return editorialListService.loadEditorialListService()
        .map(editorialListViewModel -> {
          if (!editorialListViewModel.hasError() && !editorialListViewModel.isLoading()) {
            cachedEditorialListViewModel = editorialListViewModel;
          }
          return editorialListViewModel;
        });
  }
}
