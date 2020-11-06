package cm.aptoide.pt.editorialList;

import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class EditorialCardListRepository {

  private final EditorialCardListService editorialCardListService;
  private EditorialCardListModel cachedEditorialCardListModel;

  public EditorialCardListRepository(EditorialCardListService editorialCardListService) {
    this.editorialCardListService = editorialCardListService;
  }

  public Single<EditorialCardListModel> loadEditorialCardListModel(boolean invalidateCache) {
    if (cachedEditorialCardListModel != null && !invalidateCache) {
      return Single.just(cloneList(cachedEditorialCardListModel));
    }
    return loadNewEditorialCardListModel(0, false, invalidateCache);
  }

  private Single<EditorialCardListModel> loadNewEditorialCardListModel(int offset, boolean loadMore,
      boolean invalidateCache) {
    return editorialCardListService.loadEditorialCardListModel(offset, invalidateCache)
        .map(editorialCardListModel -> {
          if (!editorialCardListModel.hasError() && !editorialCardListModel.isLoading()) {
            updateCache(editorialCardListModel, loadMore);
          }
          return cloneList(editorialCardListModel);
        });
  }

  public boolean hasMore() {
    if (cachedEditorialCardListModel != null) {
      return cachedEditorialCardListModel.getOffset() < cachedEditorialCardListModel.getTotal();
    }
    return false;
  }

  public Single<EditorialCardListModel> loadMoreCurationCards() {
    int offset = 0;
    if (cachedEditorialCardListModel != null) {
      offset = cachedEditorialCardListModel.getOffset();
    }
    return loadNewEditorialCardListModel(offset, true, false);
  }

  private void updateCache(EditorialCardListModel editorialCardListModel, boolean loadMore) {
    if (!loadMore) {
      cachedEditorialCardListModel = editorialCardListModel;
    } else {
      List<CurationCard> curationCards = cachedEditorialCardListModel.getCurationCards();
      curationCards.addAll(editorialCardListModel.getCurationCards());
      cachedEditorialCardListModel =
          new EditorialCardListModel(curationCards, editorialCardListModel.getOffset(),
              editorialCardListModel.getTotal());
    }
  }

  public void updateCache(EditorialCardListModel editorialCardListModel, List<CurationCard> curationCards) {
    cachedEditorialCardListModel = new EditorialCardListModel(curationCards, editorialCardListModel.getOffset(),
        editorialCardListModel.getTotal());
  }

  private EditorialCardListModel cloneList(EditorialCardListModel editorialCardListModel) {
    if (editorialCardListModel.hasError() || editorialCardListModel.isLoading()) {
      return editorialCardListModel;
    }
    return new EditorialCardListModel(new ArrayList<>(editorialCardListModel.getCurationCards()),
        editorialCardListModel.getOffset(), editorialCardListModel.getTotal());
  }
}
