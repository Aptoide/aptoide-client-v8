package cm.aptoide.pt.navigator;

import android.os.Bundle;

/**
 * Created by jdandrade on 02/05/2017.
 *
 * This interface provides navigation info needed for the StorePagerAdapter to listen for
 * navigation events & data.
 *
 * Each of the specified fragments should implement its own TabNavigation.
 */

public interface TabNavigation {

  int DOWNLOADS = 1;
  int UPDATES = 2;
  int STORES = 3;
  int HOME = 4;
  int BUNDLES = 5;

  Bundle getBundle();

  int getTab();
}
