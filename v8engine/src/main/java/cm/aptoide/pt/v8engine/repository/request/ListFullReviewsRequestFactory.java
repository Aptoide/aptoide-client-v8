package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;

/**
 * Created by neuro on 03-01-2017.
 */
class ListFullReviewsRequestFactory {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;

  public ListFullReviewsRequestFactory(IdsRepository idsRepository,
      AptoideAccountManager accountManager) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
  }

  public ListFullReviewsRequest newListFullReviews(String url, boolean refresh) {
    return ListFullReviewsRequest.ofAction(url, refresh, accountManager.getAccessToken(),
        idsRepository.getAptoideClientUUID());
  }
}
