package cm.aptoide.pt.v8engine.social.data;

import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class SocialManager {
  private final SocialService service;

  public SocialManager(SocialService service) {
    this.service = service;
  }

  public Single<List<Card>> getCards() {
    return service.getCards();
  }

  public Single<List<Card>> getNextCards() {
    return service.getNextCards();
  }

  public Completable clickOnBodyEvent(Card card) {

    return null;
  }
}
