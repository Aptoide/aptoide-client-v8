package cm.aptoide.pt.v8engine.view.permission;

import android.Manifest;
import rx.Observable;

public class AccountPermissionProvider {
  private final PermissionProvider permissionProvider;

  public AccountPermissionProvider(PermissionProvider permissionProvider) {
    this.permissionProvider = permissionProvider;
  }

  public void requestCameraPermission(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.CAMERA
    }, requestCode);
  }

  public void requestGalleryPermission(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }, requestCode);
  }

  public Observable<Boolean> singlePermissionResult(int requestCode) {
    return permissionProvider.permissionResults(requestCode)
        .flatMap(list -> Observable.from(list)
            .map(permission -> permission.isGranted())
            .first());
  }
}
