package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 21/12/2016.
 */

public class TimelineLoginDisplayable extends Displayable {

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_login_header_layout;
  }

  public void login(Context context) {
    AptoideAccountManager.openAccountManager(context);
  }
}
