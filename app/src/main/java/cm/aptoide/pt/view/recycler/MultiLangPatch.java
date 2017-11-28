package cm.aptoide.pt.view.recycler;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessResponse;

public class MultiLangPatch {

  private int total;
  private int totalWaiting;

  void updateTotal(BaseV7EndlessResponse response) {
    this.totalWaiting += response.getTotal();
  }

  void updateOffset() {
    total = totalWaiting;
  }

  public int getTotal() {
    return total;
  }
}
