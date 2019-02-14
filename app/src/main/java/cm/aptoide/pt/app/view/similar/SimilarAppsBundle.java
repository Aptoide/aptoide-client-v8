package cm.aptoide.pt.app.view.similar;

import cm.aptoide.pt.app.SimilarAppsViewModel;

public class SimilarAppsBundle {

  private final BundleType bundleType;
  private final SimilarAppsViewModel model;

  public SimilarAppsBundle(SimilarAppsViewModel model, BundleType bundleType) {
    this.model = model;
    this.bundleType = bundleType;
  }

  public SimilarAppsViewModel getContent() {
    return model;
  }

  public BundleType getType() {
    return bundleType;
  }

  public enum BundleType {
    APPS, APPC_APPS
  }
}
