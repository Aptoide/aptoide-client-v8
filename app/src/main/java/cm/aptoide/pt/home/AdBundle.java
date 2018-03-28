package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class AdBundle implements HomeBundle {
  private final String title;
  private final List<WrappedAdTag> ads;
  private final Event event;
  private final String tag;

  public AdBundle(String title, AdsTagWrapper ads, Event event, String tag) {
    this.title = title;
    this.ads = new ArrayList<>();
    for (GetAdsResponse.Ad ad : ads.getAds()) {
      this.ads.add(new WrappedAdTag(ad, tag));
    }
    this.event = event;
    this.tag = tag;
  }

  @Override public String getTitle() {
    return title;
  }

  @Override public List<?> getContent() {
    return ads;
  }

  @Override public BundleType getType() {
    return BundleType.ADS;
  }

  @Override public Event getEvent() {
    return event;
  }

  @Override public String getTag() {
    return tag;
  }

  public List<WrappedAdTag> getAds() {
    return ads;
  }
}
