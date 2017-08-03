package cm.aptoide.pt.model.v7;

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

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public int getNext() {
    return next;
  }

  public void setNext(int next) {
    this.next = next;
  }

  public int getHidden() {
    return hidden;
  }

  public void setHidden(int hidden) {
    this.hidden = hidden;
  }

  public boolean isLoaded() {
    return loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }
}
