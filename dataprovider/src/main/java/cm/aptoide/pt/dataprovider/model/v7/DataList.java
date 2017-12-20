/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;

/**
 * Created by neuro on 27-04-2016.
 */
public class DataList<T> {

  private int total;
  private int count;
  private int offset;
  private Integer limit;
  private int next;
  private int hidden;
  private boolean loaded;
  private List<T> list;

  public DataList() {
  }

  public int getTotal() {
    return this.total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getCount() {
    return this.count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public int getNext() {
    return this.next;
  }

  public void setNext(int next) {
    this.next = next;
  }

  public int getHidden() {
    return this.hidden;
  }

  public void setHidden(int hidden) {
    this.hidden = hidden;
  }

  public boolean isLoaded() {
    return this.loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  public List<T> getList() {
    return this.list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + this.getTotal();
    result = result * PRIME + this.getCount();
    result = result * PRIME + this.getOffset();
    final Object $limit = this.getLimit();
    result = result * PRIME + ($limit == null ? 43 : $limit.hashCode());
    result = result * PRIME + this.getNext();
    result = result * PRIME + this.getHidden();
    result = result * PRIME + (this.isLoaded() ? 79 : 97);
    final Object $list = this.getList();
    result = result * PRIME + ($list == null ? 43 : $list.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof DataList)) return false;
    final DataList other = (DataList) o;
    if (!other.canEqual(this)) return false;
    if (this.getTotal() != other.getTotal()) return false;
    if (this.getCount() != other.getCount()) return false;
    if (this.getOffset() != other.getOffset()) return false;
    final Object this$limit = this.getLimit();
    final Object other$limit = other.getLimit();
    if (this$limit == null ? other$limit != null : !this$limit.equals(other$limit)) return false;
    if (this.getNext() != other.getNext()) return false;
    if (this.getHidden() != other.getHidden()) return false;
    if (this.isLoaded() != other.isLoaded()) return false;
    final Object this$list = this.getList();
    final Object other$list = other.getList();
    return this$list == null ? other$list == null : this$list.equals(other$list);
  }

  public String toString() {
    return "DataList(total="
        + this.getTotal()
        + ", count="
        + this.getCount()
        + ", offset="
        + this.getOffset()
        + ", limit="
        + this.getLimit()
        + ", next="
        + this.getNext()
        + ", hidden="
        + this.getHidden()
        + ", loaded="
        + this.isLoaded()
        + ", list="
        + this.getList()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof DataList;
  }
}
