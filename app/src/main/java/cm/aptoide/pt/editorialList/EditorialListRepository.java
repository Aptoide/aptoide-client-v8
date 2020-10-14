package cm.aptoide.pt.editorialList;

import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class EditorialListRepository {

  private final EditorialListService editorialListService;
  private EditorialListModel cachedEditorialListModel;

  public EditorialListRepository(EditorialListService editorialListService) {
    this.editorialListService = editorialListService;
  }

  public Single<EditorialListModel> loadEditorialListModel(boolean invalidateCache) {
    if (cachedEditorialListModel != null && !invalidateCache) {
      return Single.just(cloneList(cachedEditorialListModel));
    }
    return loadNewEditorialListModel(0, false, invalidateCache);
  }

  private Single<EditorialListModel> loadNewEditorialListModel(int offset, boolean loadMore,
      boolean invalidateCache) {
    return editorialListService.loadEditorialListModel(offset, invalidateCache)
        .map(editorialListModel -> {
          if (!editorialListModel.hasError() && !editorialListModel.isLoading()) {
            updateCache(editorialListModel, loadMore);
          }
          return cloneList(editorialListModel);
        });
  }

  public boolean hasMore() {
    if (cachedEditorialListModel != null) {
      return cachedEditorialListModel.getOffset() < cachedEditorialListModel.getTotal();
    }
    return false;
  }

  public Single<EditorialListModel> loadMoreCurationCards() {
    int offset = 0;
    if (cachedEditorialListModel != null) {
      offset = cachedEditorialListModel.getOffset();
    }
    return loadNewEditorialListModel(offset, true, false);
  }

  private void updateCache(EditorialListModel editorialListModel, boolean loadMore) {
    if (!loadMore) {
      cachedEditorialListModel = editorialListModel;
    } else {
      List<CurationCard> curationCards = cachedEditorialListModel.getCurationCards();
      curationCards.addAll(editorialListModel.getCurationCards());
      cachedEditorialListModel =
          new EditorialListModel(curationCards, editorialListModel.getOffset(),
              editorialListModel.getTotal());
    }
  }

  public void updateCache(EditorialListModel editorialListModel, List<CurationCard> curationCards) {
    cachedEditorialListModel = new EditorialListModel(curationCards, editorialListModel.getOffset(),
        editorialListModel.getTotal());
  }

  private EditorialListModel cloneList(EditorialListModel editorialListModel) {
    if (editorialListModel.hasError() || editorialListModel.isLoading()) {
      return editorialListModel;
    }
    return new EditorialListModel(new ArrayList<>(editorialListModel.getCurationCards()),
        editorialListModel.getOffset(), editorialListModel.getTotal());
  }
}
