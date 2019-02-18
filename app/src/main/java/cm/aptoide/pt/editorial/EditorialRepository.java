package cm.aptoide.pt.editorial;

import rx.Single;

/**
 * Created by D01 on 29/08/2018.
 */

public class EditorialRepository {

  private final EditorialService editorialService;
  private EditorialViewModel cachedEditorialViewModel;

  public EditorialRepository(EditorialService editorialService) {
    this.editorialService = editorialService;
  }

  public Single<EditorialViewModel> loadEditorialViewModel(String cardId) {
    if (cachedEditorialViewModel != null) {
      return Single.just(cachedEditorialViewModel);
    }
    return editorialService.loadEditorialViewModel(cardId)
        .map(editorialViewModel -> {
          if (!editorialViewModel.hasError() && !editorialViewModel.isLoading()) {
            cachedEditorialViewModel = editorialViewModel;
          }
          return editorialViewModel;
        });
  }
}
