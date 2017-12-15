/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) @Data public class BaseV7EndlessTimelineDataListResponse<T>
    extends BaseV7EndlessResponse {
  @JsonProperty("datalist") private TimelineDataList<T> dataList;

  @Override public int getTotal() {
    return hasData() ? dataList.getTotal() : 0;
  }

  @Override public int getNextSize() {
    return hasData() ? dataList.getNext() : 0;
  }

  @Override public boolean hasData() {
    return dataList != null && dataList.getList() != null;
  }
}
