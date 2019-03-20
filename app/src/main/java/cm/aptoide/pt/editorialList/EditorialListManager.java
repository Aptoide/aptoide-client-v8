package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.editorial.FakeReactionModel;
import cm.aptoide.pt.editorial.FakeReactionsManager;
import java.util.List;
import rx.Observable;
import rx.Single;

public class EditorialListManager {

  private final EditorialListRepository editorialListRepository;
  private final FakeReactionsManager fakeReactionsManager;

  public EditorialListManager(EditorialListRepository editorialListRepository,
      FakeReactionsManager fakeReactionsManager) {
    this.editorialListRepository = editorialListRepository;
    this.fakeReactionsManager = fakeReactionsManager;
  }

  Single<EditorialListViewModel> loadEditorialListViewModel(boolean loadMore,
      boolean invalidateCache) {
    if (loadMore) {
      return loadMoreCurationCards();
    } else {
      return editorialListRepository.loadEditorialListViewModel(invalidateCache);
    }
  }

  public boolean hasMore() {
    return editorialListRepository.hasMore();
  }

  private Single<EditorialListViewModel> loadMoreCurationCards() {
    return editorialListRepository.loadMoreCurationCards();
  }

  public Observable<List<CurationCard>> loadReactionModel(String cardId) {
    return fakeReactionsManager.loadReactionModel(cardId)
        .flatMap(reactionModel -> editorialListRepository.loadEditorialListViewModel(false)
            .toObservable()
            .flatMap(editorialListViewModel -> updateCards(reactionModel,
                editorialListViewModel.getCurationCards())));
  }

  private Observable<List<CurationCard>> updateCards(FakeReactionModel reactionModel,
      List<CurationCard> curationCards) {
    for (CurationCard curationCard : curationCards) {
      if (curationCard.getId()
          .equals(reactionModel.getCardId())) {
        curationCard.setNumberOfReactions(reactionModel.getNumberOfReactions());
        curationCard.setReactionTypes(reactionModel.getReactionTypes());
      }
    }
    return Observable.just(curationCards);
  }
}
