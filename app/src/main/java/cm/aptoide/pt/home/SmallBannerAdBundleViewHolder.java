package cm.aptoide.pt.home;

import android.view.View;

class SmallBannerAdBundleViewHolder extends AppBundleViewHolder {

  private boolean hasLoaded;

  public SmallBannerAdBundleViewHolder(View view) {
    super(view);
    hasLoaded = false;
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!hasLoaded) {
      hasLoaded = true;
    }
  }
}
