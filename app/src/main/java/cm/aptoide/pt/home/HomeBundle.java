package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.List;

/**
 * Created by jdandrade on 13/03/2018.
 */

public interface HomeBundle {

  String getTitle();

  List<?> getContent();

  BundleType getType();

  Event getEvent();

  String getTag();

  enum BundleType {
    EDITORS, APPS, ADS, UNKNOWN, LOADING, STORE, SOCIAL, INFO_BUNDLE, APPCOINS_ADS, EDITORIAL, SMALL_BANNER;

    public boolean isApp() {
      return this.equals(APPS) || this.equals(EDITORS) || this.equals(ADS) || this.equals(
          APPCOINS_ADS);
    }
  }
}
