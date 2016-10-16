/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by sithengineer on 29/07/16.
 */
public class SetReviewRatingRequest extends V7<BaseV7Response, SetReviewRatingRequest.Body> {

  private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";

  protected SetReviewRatingRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SetReviewRatingRequest of(long reviewId, boolean helpful, String accessToken) {
    //
    //  http://ws75-primary.aptoide.com/api/7/setReview/package_name/cm.aptoide
    // .pt/store_name/apps/title/Best%20app%20store/rating/5/access_token/ca01ee1e05ab4d82d99ef143e2816e667333c6ef
    //
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepository(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()));
    Body body = new Body(reviewId, helpful ? "up" : "down");
    return new SetReviewRatingRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReviewVote(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private long review_id;
    private String vote;

    public Body(long reviewId, String vote) {

      this.review_id = reviewId;
      this.vote = vote;
    }
  }
}
