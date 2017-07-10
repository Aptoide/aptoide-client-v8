package cm.aptoide.pt.v8engine.view.permission;

import android.Manifest;
import java.util.List;
import rx.Observable;

public class AccountPermissionProvider {

  private final PermissionProvider permissionProvider;

  public AccountPermissionProvider(PermissionProvider permissionProvider) {
    this.permissionProvider = permissionProvider;
  }

  public void requestCameraPermission(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    }, requestCode);
  }

  public void requestGalleryPermission(int requestCode) {
    permissionProvider.providePermissions(new String[] {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }, requestCode);
  }

  public Observable<List<PermissionProvider.Permission>> permissionResultCamera(int requestCode) {
    return permissionProvider.permissionResults(requestCode);
  }
}
