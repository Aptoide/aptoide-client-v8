package cm.aptoide.pt.app.view.donations.data;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

public class GetWalletAddressResponse extends BaseV7Response {

  private Data data;

  public GetWalletAddressResponse() {
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {
    private String address;

    public Data() {
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
  }
}
