/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created on 18/08/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class BaseV7EndlessDatalistResponse<T>
    extends BaseV7EndlessResponse {

  private Datalist<T> datalist;

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
