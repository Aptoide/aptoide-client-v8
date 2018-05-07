package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsService {

  private final ListReviewsRequest listReviewsRequest;

  public ReviewsService(ListReviewsRequest listReviewsRequest) {

    this.listReviewsRequest = listReviewsRequest;
  }
}
