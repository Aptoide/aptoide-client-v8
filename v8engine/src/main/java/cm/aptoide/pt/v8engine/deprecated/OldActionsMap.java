package cm.aptoide.pt.v8engine.deprecated;

import android.support.v4.util.ArrayMap;
import cm.aptoide.pt.database.realm.Rollback;

/**
 * Created on 24/10/2016.
 */

public final class OldActionsMap {

  private static final ArrayMap<String, Rollback.Action> actionMap;

  static {
    actionMap = new ArrayMap<>(8);
    // non migrating rollback actions
    actionMap.put("Installing", null);
    actionMap.put("Uninstalling", null);
    actionMap.put("Updating", null);
    actionMap.put("Downgrading", null);
    // migrating rollback actions
    actionMap.put("Installed", cm.aptoide.pt.database.realm.Rollback.Action.INSTALL);
    actionMap.put("Uninstalled", cm.aptoide.pt.database.realm.Rollback.Action.UNINSTALL);
    actionMap.put("Updated", cm.aptoide.pt.database.realm.Rollback.Action.UPDATE);
    actionMap.put("Downgraded", cm.aptoide.pt.database.realm.Rollback.Action.DOWNGRADE);
  }

  public static cm.aptoide.pt.database.realm.Rollback.Action getActionFor(String actionAsString) {
    return actionMap.get(actionAsString);
  }
}
