package cm.aptoide.pt.v8engine.spotandshare;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupNameProvider;

/**
 * Created by filipe on 06-04-2017.
 */

public abstract class SpotAndShareApplication extends DataProvider {

  public abstract GroupNameProvider getGroupNameProvider();
}
