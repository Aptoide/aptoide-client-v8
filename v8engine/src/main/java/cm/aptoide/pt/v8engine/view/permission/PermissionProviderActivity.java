package cm.aptoide.pt.v8engine.view.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

public class PermissionProviderActivity extends PermissionServiceActivity
    implements PermissionProvider {

  private PublishRelay<List<Permission>> permissionRelay;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.permissionRelay = PublishRelay.create();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionNames,
      @NonNull int[] grantResults) {
    LinkedList<Permission> permissions = new LinkedList<>();
    for (int i = 0; i < permissionNames.length; ++i) {
      permissions.add(new Permission(requestCode, permissionNames[i],
          grantResults[i] == PackageManager.PERMISSION_GRANTED));
    }
    permissionRelay.call(permissions);
  }

  @Override public void providePermissions(@NonNull String[] permissions, int requestCode) {
    ActivityCompat.requestPermissions(this, permissions, requestCode);
  }

  @Override public Observable<List<Permission>> permissionResults(int requestCode) {
    return permissionRelay.flatMap(permissions -> Observable.from(permissions)
        .filter(permission -> permission.getRequestCode() == requestCode)
        .toList())
        .filter(permissions -> !permissions.isEmpty());
  }
}
