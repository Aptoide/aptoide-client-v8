package cm.aptoide.pt.spotandshareapp;

import android.Manifest;
import cm.aptoide.pt.v8engine.view.permission.PermissionProvider;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 20-07-2017.
 */

public class SpotAndSharePermissionProvider {

  private final PermissionProvider permissionProvider;

  public SpotAndSharePermissionProvider(PermissionProvider permissionProvider) {
    this.permissionProvider = permissionProvider;
  }

  public void requestNormalSpotAndSharePermissions(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_SETTINGS
    }, requestCode);
  }

  public Observable<List<PermissionProvider.Permission>> normalPermissionResultSpotAndShare(
      int requestCode) {
    return permissionProvider.permissionResults(requestCode);
  }
}
