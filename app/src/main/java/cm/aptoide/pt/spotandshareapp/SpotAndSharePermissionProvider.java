package cm.aptoide.pt.spotandshareapp;

import android.Manifest;
import cm.aptoide.pt.permission.PermissionProvider;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 20-07-2017.
 */

public class SpotAndSharePermissionProvider {

  private final PermissionProvider permissionProvider;
  private final WriteSettingsPermissionProvider writeSettingsPermissionProvider;

  public SpotAndSharePermissionProvider(PermissionProvider permissionProvider,
      WriteSettingsPermissionProvider writeSettingsPermissionProvider) {
    this.permissionProvider = permissionProvider;
    this.writeSettingsPermissionProvider = writeSettingsPermissionProvider;
  }

  public void requestLocationAndExternalStorageSpotAndSharePermissions(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
    }, requestCode);
  }

  public Observable<List<PermissionProvider.Permission>> locationAndExternalStoragePermissionsResultSpotAndShare(
      int requestCode) {
    return permissionProvider.permissionResults(requestCode);
  }

  public Observable<Integer> writeSettingsPermissionResult() {
    return writeSettingsPermissionProvider.permissionResult();
  }

  public void requestWriteSettingsPermission(int requestCode) {
    writeSettingsPermissionProvider.requestWriteSettingsPermission(requestCode);
  }
}
