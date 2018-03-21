package cm.aptoide.pt.home;

/**
 * Created by jdandrade on 14/03/2018.
 */

class ProgressBundle extends DummyBundle {
  @Override public BundleType getType() {
    return BundleType.LOADING;
  }
}
