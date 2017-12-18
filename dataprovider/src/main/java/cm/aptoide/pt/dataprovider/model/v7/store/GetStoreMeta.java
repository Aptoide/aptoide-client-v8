/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreMeta extends BaseV7Response {

  private Store data;

  public GetStoreMeta() {
  }

  public Store getData() {
    return this.data;
  }

  public void setData(Store data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetStoreMeta)) return false;
    final GetStoreMeta other = (GetStoreMeta) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetStoreMeta;
  }

  public String toString() {
    return "GetStoreMeta(data=" + this.getData() + ")";
  }
}
