package cm.aptoide.pt.home;

import java.util.List;

/**
 * Created by jdandrade on 13/03/2018.
 */

public interface HomeBundle {

  String getTitle();

  List<?> getContent();

  BundleType getType();

  enum BundleType {
    EDITORS, APPS, ADS, STORE
  }
}
