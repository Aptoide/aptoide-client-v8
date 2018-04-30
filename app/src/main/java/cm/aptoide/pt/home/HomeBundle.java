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
    EDITORS, APPS, ADS, ERROR, LOADING, STORE, SOCIAL, APPCOINS_ADS
  }
}
