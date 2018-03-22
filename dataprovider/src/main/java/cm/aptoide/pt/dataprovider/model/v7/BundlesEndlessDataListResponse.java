package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 09/03/2018.
 */

public class BundlesEndlessDataListResponse<T> extends BaseV7EndlessResponse {
  @JsonProperty("datalist") private BundlesDataList dataList;

  public BundlesEndlessDataListResponse() {
  }

  @Override public int getTotal() {
    return hasData() ? dataList.getTotal() : 0;
  }

  @Override public int getNextSize() {
    return hasData() ? dataList.getNext() : 0;
  }

  @Override public boolean hasData() {
    return dataList != null && dataList.getList() != null;
  }

  public BundlesDataList getDataList() {
    return dataList;
  }

  public void setDataList(BundlesDataList dataList) {
    this.dataList = dataList;
  }
}
