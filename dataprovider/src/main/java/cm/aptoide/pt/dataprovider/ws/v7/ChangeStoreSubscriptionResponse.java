package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.base.BaseV7Response;
import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;

/**
 * Created by trinkes on 03/03/2017.
 */

@Data public class ChangeStoreSubscriptionResponse extends BaseV7Response {

  StoreSubscriptionState status;
  Store store;

  public enum StoreSubscriptionState {
    UNSUBSCRIBED, SUBSCRIBED
  }
}
