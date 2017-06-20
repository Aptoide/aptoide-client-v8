package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.Store;

/**
 * Created by trinkes on 03/03/2017.
 */
public class ChangeStoreSubscriptionResponse extends BaseV7Response {

  private StoreSubscriptionState status;
  private Store store;

  public StoreSubscriptionState getStatus() {
    return status;
  }

  public void setStatus(StoreSubscriptionState status) {
    this.status = status;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public enum StoreSubscriptionState {
    UNSUBSCRIBED, SUBSCRIBED
  }
}
