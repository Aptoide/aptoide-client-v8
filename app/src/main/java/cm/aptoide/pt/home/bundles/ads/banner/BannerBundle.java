package cm.aptoide.pt.home.bundles.ads.banner;

import cm.aptoide.pt.home.bundles.base.DummyBundle;
import java.util.ArrayList;
import java.util.List;

public class BannerBundle extends DummyBundle {

  @Override public BundleType getType() {
    return BundleType.SMALL_BANNER;
  }

  @Override public List<?> getContent() {
    return new ArrayList<>();
  }
}
