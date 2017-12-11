package cm.aptoide.pt.account.view.exception;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.List;

/**
 * Created by pedroribeiro on 26/10/17.
 */

public class SocialLinkException extends Exception {
  private List<BaseV7Response.StoreLinks> storeLinks;

  public SocialLinkException(List<BaseV7Response.StoreLinks> storeLinks) {
    this.storeLinks = storeLinks;
  }

  public List<BaseV7Response.StoreLinks> getStoreLinks() {
    return storeLinks;
  }
}
