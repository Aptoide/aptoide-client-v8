package cm.aptoide.pt.home.bundles.ads;

import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;

/**
 * Created by franciscocalado on 03/07/2018.
 */

public class AdHomeEvent extends HomeEvent {

  private final AdClick adClick;
  private final int position;

  public AdHomeEvent(AdClick ad, int position, HomeBundle bundle, int bundlePosition,
      Type clickType) {
    super(bundle, bundlePosition, clickType);
    this.adClick = ad;
    this.position = position;
  }

  public AdClick getAdClick() {
    return adClick;
  }

  public int getPosition() {
    return position;
  }
}
