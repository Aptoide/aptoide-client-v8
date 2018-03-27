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
  int TIMELINE = 3;
  int STORES = 4;
  int COMMENTS = 5;
  int HOME = 6;
  int BUNDLES = 7;

  Bundle getBundle();

  int getTab();
}
