package cm.aptoide.pt.model.v7.base;

import cm.aptoide.pt.model.v7.DataList;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created on 18/08/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class BaseV7EndlessDataListResponse<T>
    extends BaseV7EndlessResponse {

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
}
