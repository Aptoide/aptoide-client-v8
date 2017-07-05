package cm.aptoide.pt.v8engine.view.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseIntArray;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class PermissionProviderActivity extends PermissionServiceActivity
    implements PermissionProvider {

  private SparseIntArray permissionRequests;
  private PublishRelay<Permission> permissionRelay;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    permissionRequests = new SparseIntArray();
    this.permissionRelay = PublishRelay.create();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionNames,
      @NonNull int[] grantResults) {
    for (int i = 0; i < permissionNames.length; ++i) {
      permissionRelay.call(new Permission(requestCode, permissionNames[i],
          grantResults[i] == PackageManager.PERMISSION_GRANTED));
    }
  }

  @Override public void providePermissions(@NonNull String[] permissions, int requestCode) {
    final List<String> remainderPermissions = new ArrayList<>();

    permissionRequests.put(requestCode, permissions.length);

    for (String permission : permissions) {
      if (ActivityCompat.checkSelfPermission(this, permission)
          == PackageManager.PERMISSION_GRANTED) {
        permissionRelay.call(new Permission(requestCode, permission, true));
      } else {
        remainderPermissions.add(permission);
      }
    }

    if (remainderPermissions.size() > 0) {
      ActivityCompat.requestPermissions(this, remainderPermissions.toArray(new String[0]),
          requestCode);
    }
  }

  @Override public Observable<List<Permission>> permissionResults(int requestCode) {
    return permissionRelay.filter(permission -> permission.getRequestCode() == requestCode)
        .buffer(permissionRequests.get(requestCode));
  }
}
