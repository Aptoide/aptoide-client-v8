/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

/**
 * Created by neuro on 23-05-2016.
 */
public class BaseBodyWithStore extends BaseBody {

  private Long storeId;
  private String storeName;
  private String storeUser;
  private String storePassSha1;

  public BaseBodyWithStore() {
  }

  public BaseBodyWithStore(BaseRequestWithStore.StoreCredentials storeCredentials) {
    this.storeId = storeCredentials.getId();
    this.storeName = storeCredentials.getName();
    this.storeUser = storeCredentials.getUsername();
    this.storePassSha1 = storeCredentials.getPasswordSha1();
  }

  public Long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreUser() {
    return storeUser;
  }

  public String getStorePassSha1() {
    return storePassSha1;
  }
}
