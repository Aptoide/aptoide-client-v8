package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.app.AppCoinsManager;
import cm.aptoide.pt.reactions.ReactionsManager;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import java.util.List;
import rx.Single;

public class EditorialListManager {

  private final EditorialCardListRepository editorialCardListRepository;
  private final ReactionsManager reactionsManager;
  private final AppCoinsManager appCoinsManager;

  public EditorialListManager(EditorialCardListRepository editorialCardListRepository,
      ReactionsManager reactionsManager, AppCoinsManager appCoinsManager) {
    this.editorialCardListRepository = editorialCardListRepository;
    this.reactionsManager = reactionsManager;
    this.appCoinsManager = appCoinsManager;
  }

  Single<EditorialListModel> loadEditorialListModel(boolean loadMore, boolean invalidateCache) {
    if (loadMore) {
      return loadMoreCurationCards().flatMap(
          editorialCardListModel -> appCoinsManager.getBonusAppc()
              .map(bonusAppcModel -> new EditorialListModel(editorialCardListModel,
                  bonusAppcModel)));
    } else {
      return editorialCardListRepository.loadEditorialCardListModel(invalidateCache)
          .flatMap(editorialCardListModel -> appCoinsManager.getBonusAppc()
              .map(bonusAppcModel -> new EditorialListModel(editorialCardListModel,
                  bonusAppcModel)));
    }
  }

  public boolean hasMore() {
    return editorialCardListRepository.hasMore();
  }

  private Single<EditorialCardListModel> loadMoreCurationCards() {
    return editorialCardListRepository.loadMoreCurationCards();
  }

  public Single<CurationCard> loadReactionModel(String cardId, String groupId) {
    return reactionsManager.loadReactionModel(cardId, groupId)
        .flatMap(loadReactionModel -> editorialCardListRepository.loadEditorialCardListModel(false)
            .flatMap(editorialListModel -> getUpdatedCards(editorialListModel, loadReactionModel,
                cardId)));
  }

  private Single<CurationCard> getUpdatedCards(EditorialCardListModel editorialCardListModel,
      LoadReactionModel loadReactionModel, String cardId) {
    List<CurationCard> curationCards = editorialCardListModel.getCurationCards();
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
    editorialCardListRepository.updateCache(editorialCardListModel, curationCards);
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
