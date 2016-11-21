/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by sithengineer on 20/07/16.
 */
public class PostCommentForStore extends
    V7<BaseV7Response, PostCommentForStore.Body> {

  private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";

  protected PostCommentForStore(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static PostCommentForStore of(long reviewId, String text, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(reviewId, text);
    return new PostCommentForStore((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postStoreComment(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private long storeId;
    private String body;

    public Body(long storeId, String text) {
      this.storeId = storeId;
      this.body = text;
    }
  }
}
