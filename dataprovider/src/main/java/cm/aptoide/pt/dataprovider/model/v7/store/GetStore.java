/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

public class GetStore extends StoreUserAbstraction<GetStoreMeta> {

  public GetStore() {
  }

  public String toString() {
    return "GetStore()";
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetStore)) return false;
    final GetStore other = (GetStore) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetStore;
  }
}
