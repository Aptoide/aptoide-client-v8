package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.spotandshareandroid.group.GroupNameProvider;

/**
 * Created by filipe on 06-04-2017.
 */

public abstract class SpotAndShareApplication extends DataProvider {

  public abstract GroupNameProvider getGroupNameProvider();
}
