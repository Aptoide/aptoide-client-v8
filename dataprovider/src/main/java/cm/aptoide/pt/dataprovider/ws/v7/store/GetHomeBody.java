package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by trinkes on 24/02/2017.
 */

public class GetHomeBody extends GetStoreBody {
  @Getter @Setter private Long userId;

  public GetHomeBody(BaseRequestWithStore.StoreCredentials storeCredentials,
      WidgetsArgs widgetsArgs, Long userId) {
    super(storeCredentials, widgetsArgs);
    this.userId = userId;
  }
}
