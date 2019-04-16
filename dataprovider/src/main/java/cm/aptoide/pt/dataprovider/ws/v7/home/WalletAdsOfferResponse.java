package cm.aptoide.pt.dataprovider.ws.v7.home;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WalletAdsOfferResponse extends BaseV7Response {

  private WalletAdOffer data;

  public WalletAdOffer getData() {
    return this.data;
  }

  public void setData(WalletAdOffer data) {
    this.data = data;
  }

  public class WalletAdOffer {

    @JsonProperty("remove_ads") private boolean isWalletOfferActive;

    public WalletAdOffer() {
    }

    public boolean isOfferActive() {
      return isWalletOfferActive;
    }

    public void setWalletOfferStatus(boolean isWalletOfferActive) {
      this.isWalletOfferActive = isWalletOfferActive;
    }
  }
}
