package cm.aptoide.pt.ads.model;

import android.view.View;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public interface ApplicationAd {
  String getAdTitle();
  String getIconUrl();
  Integer getStars();
  void registerClickableView(View view);
}
