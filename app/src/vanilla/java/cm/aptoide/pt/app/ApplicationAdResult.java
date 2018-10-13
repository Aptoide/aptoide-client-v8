package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.model.ApplicationAd;
import cm.aptoide.pt.ads.model.ApplicationAdError;

/**
 * Created by franciscoaleixo on 08/10/2018.
 */

public interface ApplicationAdResult {
  ApplicationAd getAd();
  ApplicationAdError getError();
}
