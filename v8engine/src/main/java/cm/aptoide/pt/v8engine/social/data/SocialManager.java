package cm.aptoide.pt.v8engine.social.data;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class SocialManager {
  private final SocialService service;

  public SocialManager(SocialService service) {
    this.service = service;
  }

  public Single<List<Media>> getCards() {
    return service.getCards();
  }

  public Single<List<Media>> getNextCards() {
    return service.getNextCards();
  }
}
