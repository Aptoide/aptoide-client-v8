package cm.aptoide.pt.editorialList;

import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;

  public EditorialListManager(EditorialListRepository editorialListRepository) {
    this.editorialListRepository = editorialListRepository;
  }

  public Single<EditorialListViewModel> loadEditorialListViewModel() {
    return editorialListRepository.loadEditorialListViewModel();
  }
}
