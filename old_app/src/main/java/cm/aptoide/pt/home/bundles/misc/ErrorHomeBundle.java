package cm.aptoide.pt.home.bundles.misc;

import cm.aptoide.pt.home.bundles.base.DummyBundle;

public class ErrorHomeBundle extends DummyBundle {
  @Override public BundleType getType() {
    return BundleType.LOAD_MORE_ERROR;
  }
}
