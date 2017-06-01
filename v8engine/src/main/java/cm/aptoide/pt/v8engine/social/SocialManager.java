package cm.aptoide.pt.v8engine.social;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

class SocialManager {
  private final SocialService service;

  SocialManager(SocialService service) {
    this.service = service;
  }

  public Single<List<Article>> getCards(int limit, int offset) {
    return service.getCards(limit, offset);
  }
}
