/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;

/**
 * Created on 18/08/16.
 */
public class BaseV7EndlessListResponse<T> extends BaseV7EndlessResponse {

  private List<T> list;

  public BaseV7EndlessListResponse() {
    super(false);
  }

  @Override public int getTotal() {
    return list != null ? list.size() : 0;
  }

  @Override public int getNextSize() {
    return list != null ? NEXT_STEP : 0;
  }

  @Override public boolean hasData() {
    return list != null;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $list = this.getList();
    result = result * PRIME + ($list == null ? 43 : $list.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BaseV7EndlessListResponse)) return false;
    final BaseV7EndlessListResponse other = (BaseV7EndlessListResponse) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$list = this.getList();
    final Object other$list = other.getList();
    if (this$list == null ? other$list != null : !this$list.equals(other$list)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof BaseV7EndlessListResponse;
  }

  public List<T> getList() {
    return this.list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public String toString() {
    return "BaseV7EndlessListResponse(list=" + this.getList() + ")";
  }
}
