package cm.aptoide.pt.spotandshareapp;

import rx.Observable;

/**
 * Created by filipe on 20-07-2017.
 */

public interface WriteSettingsPermissionProvider {

  void requestWriteSettingsPermission(int requestCode);

  Observable<Integer> permissionResult();
}
