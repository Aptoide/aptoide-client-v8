package cm.aptoide.pt.v8engine.view.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rx.Observable;

public abstract class PermissionProviderActivity extends PermissionServiceActivity
    implements PermissionProvider {

  private PublishRelay<Set<Permission>> permissionRelay;
  private SparseArray<Set<Permission>> requestedCodeGrantedPermissions;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.permissionRelay = PublishRelay.create();
    this.requestedCodeGrantedPermissions = new SparseArray<>();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionNames,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissionNames, grantResults);

    Set<Permission> permissions = new HashSet<>();
    for (int i = 0; i < permissionNames.length; ++i) {
      permissions.add(new Permission(requestCode, permissionNames[i],
          grantResults[i] == PackageManager.PERMISSION_GRANTED));
    }
    permissionRelay.call(permissions);
  }

  @Override public void providePermissions(@NonNull String[] permissions, int requestCode) {

    final Set<Permission> grantedPermissions = new HashSet<>();
    final List<String> notGrantedPermissions = new ArrayList<>();
    this.requestedCodeGrantedPermissions.clear();

    for (String permission : permissions) {
      if (ActivityCompat.checkSelfPermission(this, permission)
          == PackageManager.PERMISSION_GRANTED) {

        grantedPermissions.add(new Permission(requestCode, permission, true));
      } else {
        notGrantedPermissions.add(permission);
      }
    }

    this.requestedCodeGrantedPermissions.put(requestCode, grantedPermissions);

    if (notGrantedPermissions.isEmpty()) {
      permissionRelay.call(grantedPermissions);
    } else {
      ActivityCompat.requestPermissions(this, notGrantedPermissions.toArray(new String[0]),
          requestCode);
    }
  }

  @Override public Observable<List<Permission>> permissionResults(int requestCode) {
    return permissionRelay.flatMap(permissions -> Observable.zip(Observable.just(permissions),
        Observable.just(this.requestedCodeGrantedPermissions.get(requestCode)),
        (systemPermissions, savedPermissions) -> {
          systemPermissions.addAll(savedPermissions);
          return systemPermissions;
        })
        .flatMapIterable(mergedPermissions -> mergedPermissions)
        .filter(permission -> requestCode == permission.getRequestCode())
        .toList()
        .filter(permissionsList -> !permissionsList.isEmpty()));
  }
}
