package cm.aptoide.pt.spotandshareandroid;

import android.app.Application;

/**
 * Created by filipe on 06-04-2017.
 */

public abstract class SpotAndShareApplication extends Application {

  public abstract GroupNameProvider getGroupNameProvider();
}
