package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.BaseV7Response;

/**
 * Created by jdandrade on 13/03/2017.
 */

public class ShareCardResponse extends BaseV7Response {
  private Data data;

  public ShareCardResponse() {
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private String cardUid;

    public Data() {

    }

    public String getCardUid() {
      return cardUid;
    }

    public void setCardUid(String cardUid) {
      this.cardUid = cardUid;
    }
  }
}
