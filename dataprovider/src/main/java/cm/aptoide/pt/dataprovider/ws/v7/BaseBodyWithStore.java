/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 23-05-2016.
 */
@EqualsAndHashCode(callSuper = true) public class BaseBodyWithStore extends BaseBody {

  @Getter private final Long storeId;
  @Getter private final String storeName;
  @Getter private final String storeUser;
  @Getter private final String storePassSha1;

  public BaseBodyWithStore(BaseRequestWithStore.StoreCredentials storeCredentials) {
    this.storeId = storeCredentials.getId();
    this.storeName = storeCredentials.getName();
    this.storeUser = storeCredentials.getUsername();
    this.storePassSha1 = storeCredentials.getPasswordSha1();
  }
}
