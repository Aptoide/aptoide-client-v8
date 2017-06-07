package cm.aptoide.pt.v8engine.view.account.store;

import rx.Observable;

/**
 * To create a store we need to call WS CheckUserCredentials so we can associate a
 * user to a newly created store.
 *
 * Then, if we have more data we either use a SetStore with multi-part request if we have
 * a store image, or a SetStore without image.
 */
public class CreateStoreUseCase {

  private final ManageStoreModel storeModel;

  public CreateStoreUseCase(ManageStoreModel storeModel) {
    this.storeModel = storeModel;
  }

  public Observable<Void> execute() {
    return Observable.empty();
  }
}
