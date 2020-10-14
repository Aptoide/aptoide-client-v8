package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.reactions.ReactionsManager;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import java.util.List;
import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;
  private final ReactionsManager reactionsManager;

  public EditorialListManager(EditorialListRepository editorialListRepository,
      ReactionsManager reactionsManager) {
    this.editorialListRepository = editorialListRepository;
    this.reactionsManager = reactionsManager;
  }

  Single<EditorialListModel> loadEditorialListModel(boolean loadMore, boolean invalidateCache) {
    if (loadMore) {
      return loadMoreCurationCards();
    } else {
      return editorialListRepository.loadEditorialListModel(invalidateCache);
    }
  }

  public boolean hasMore() {
    return editorialListRepository.hasMore();
  }

  private Single<EditorialListModel> loadMoreCurationCards() {
    return editorialListRepository.loadMoreCurationCards();
  }

  public Single<CurationCard> loadReactionModel(String cardId, String groupId) {
    return reactionsManager.loadReactionModel(cardId, groupId)
        .flatMap(loadReactionModel -> editorialListRepository.loadEditorialListModel(false)
            .flatMap(
                editorialListModel -> getUpdatedCards(editorialListModel, loadReactionModel,
                    cardId)));
  }

  private Single<CurationCard> getUpdatedCards(EditorialListModel editorialListModel,
      LoadReactionModel loadReactionModel, String cardId) {
    List<CurationCard> curationCards = editorialListModel.getCurationCards();
    CurationCard changedCurationCard = null;
    for (CurationCard curationCard : curationCards) {
      if (curationCard.getId()
          .equals(cardId)) {
        curationCard.setReactions(loadReactionModel.getTopReactionList());
        curationCard.setNumberOfReactions(loadReactionModel.getTotal());
        curationCard.setUserReaction(loadReactionModel.getMyReaction());
        changedCurationCard = curationCard;
        break;
      }
    }
    editorialListRepository.updateCache(editorialListModel, curationCards);
    return Single.just(changedCurationCard);
  }

  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reaction) {
    return reactionsManager.setReaction(cardId, groupId, reaction);
  }

  public Single<ReactionsResponse> deleteReaction(String cardId, String groupId) {
    return reactionsManager.deleteReaction(cardId, groupId);
  }

  public Single<Boolean> isFirstReaction(String cardId, String groupId) {
    return reactionsManager.isFirstReaction(cardId, groupId);
  }
}
