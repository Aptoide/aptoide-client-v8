package cm.aptoide.pt.model.v7.base;

import cm.aptoide.pt.model.v7.DataList;

public class BaseV7EndlessDataListResponse<T> extends BaseV7EndlessResponse {

  private DataList<T> datalist;

  @Override public int getTotal() {
    return hasData() ? datalist.getTotal() : 0;
  }

  @Override public int getNextSize() {
    return hasData() ? datalist.getNext() : 0;
  }

  @Override public boolean hasData() {
    return datalist != null && datalist.getList() != null;
  }

  public DataList<T> getDatalist() {
    return datalist;
  }

  public void setDatalist(DataList<T> datalist) {
    this.datalist = datalist;
  }
}
