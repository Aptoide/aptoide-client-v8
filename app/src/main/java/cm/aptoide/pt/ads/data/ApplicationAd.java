package cm.aptoide.pt.ads.data;

import android.view.View;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public interface ApplicationAd {
  String getAdTitle();

  String getIconUrl();

  Integer getStars();

  void registerClickableView(View view);

  String getPackageName();

  Network getNetwork();

  void setAdView(View adView);

  enum Network {
    SERVER("Server"), MOPUB("MoPub");
    private String name;

    Network(String network) {
      this.name = network;
    }

    public String getName() {
      return name;
    }
  }
}
