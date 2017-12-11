package cm.aptoide.pt.account.view.store;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import org.parceler.Parcel;

/**
 * Created by pedroribeiro on 10/11/17.
 */

@Parcel public class SocialLink {

  Store.SocialChannelType type;
  String url;

  public SocialLink() {
  }

  public SocialLink(Store.SocialChannelType type, String url) {
    this.type = type;
    this.url = url;
  }

  public Store.SocialChannelType getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }
}
