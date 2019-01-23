package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.ArrayList;
import java.util.List;

public class BannerBundle implements HomeBundle {
  @Override public String getTitle() {
    return "Advertising";
  }

  @Override public List<?> getContent() {
    ArrayList<Object> objects = new ArrayList<>();
    objects.add(new BannerAd());
    return objects;
  }

  @Override public BundleType getType() {
    return BundleType.SMALL_BANNER;
  }

  @Override public Event getEvent() {
    return null;
  }

  @Override public String getTag() {
    return null;
  }
}
