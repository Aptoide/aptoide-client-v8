package cm.aptoide.pt.account.view;

import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 13/09/2017.
 */

public class LoginDisplayable extends Displayable {
  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.login_store_displayable_layout;
  }
}
