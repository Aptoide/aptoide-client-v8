package cm.aptoide.pt.v8engine.view.account.store;

import rx.Observable;

/**
 * If we have more data we either use a SetStore with multi-part request if we have
 * a store image, or a SetStore without image.
 */
public class EditStoreUseCase {

  private final ManageStoreModel storeModel;

  public EditStoreUseCase(ManageStoreModel storeModel) {
    this.storeModel = storeModel;
  }

  public Observable<Void> execute() {
    return Observable.empty();
  }
}
