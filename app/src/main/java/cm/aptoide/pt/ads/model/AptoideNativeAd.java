package cm.aptoide.pt.ads.model;

import android.view.View;
import cm.aptoide.pt.database.realm.MinimalAd;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class AptoideNativeAd implements ApplicationAd {
  private final MinimalAd minimalAd;

  public AptoideNativeAd(MinimalAd minimalAd) {
    this.minimalAd = minimalAd;
  }

  @Override public String getAdTitle() {
    return minimalAd.getName();
  }

  @Override public String getIconUrl() {
    return minimalAd.getIconPath();
  }

  @Override public Integer getStars() {
    return minimalAd.getStars();
  }

  @Override public void registerClickableView(View view) {}


}
