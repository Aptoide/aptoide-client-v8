package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by trinkes on 23/02/2017.
 */
@EqualsAndHashCode(callSuper = true) public class GetHomeAndStoreBody extends BaseBodyWithStore {

  @Getter private final WidgetsArgs widgetsArgs;
  @Getter @Setter private StoreContext context;

  public GetHomeAndStoreBody(BaseRequestWithStore.StoreCredentials storeCredentials,
      WidgetsArgs widgetsArgs) {
    super(storeCredentials);
    this.widgetsArgs = widgetsArgs;
  }
}
