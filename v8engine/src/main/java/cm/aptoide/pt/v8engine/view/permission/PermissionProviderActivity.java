package cm.aptoide.pt.v8engine.view.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class PermissionProviderActivity extends PermissionServiceActivity
    implements PermissionProvider {

  private PublishRelay<PermissionTicket> permissionTickets;
  private PublishRelay<Permission> permissionRelay;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.permissionRelay = PublishRelay.create();
    this.permissionTickets = PublishRelay.create();
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

    permissionTickets.call(new PermissionTicket(requestCode, permissions.length));

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
    return permissionTickets.flatMap(
        ticket -> permissionRelay.filter(permission -> permission.getRequestCode() == requestCode)
            .buffer(ticket.getRequestLength()));
  }

  private static final class PermissionTicket {
    private final int requestCode;
    private final int requestLength;

    private PermissionTicket(int requestCode, int requestLength) {
      this.requestCode = requestCode;
      this.requestLength = requestLength;
    }

    public int getRequestCode() {
      return requestCode;
    }

    public int getRequestLength() {
      return requestLength;
    }
  }
}
