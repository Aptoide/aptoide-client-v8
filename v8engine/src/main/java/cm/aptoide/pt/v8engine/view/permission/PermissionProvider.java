package cm.aptoide.pt.v8engine.view.permission;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import java.util.List;
import rx.Observable;

public interface PermissionProvider {

  void providePermissions(final @NonNull String[] permissions,
      final @IntRange(from = 0) int requestCode);

  Observable<List<Permission>> permissionResults(int requestCode);

  class Permission {
    private final int requestCode;
    private final String name;
    private final boolean granted;

    public Permission(int requestCode, String name, boolean granted) {
      this.requestCode = requestCode;
      this.name = name;
      this.granted = granted;
    }

    public int getRequestCode() {
      return requestCode;
    }

    public String getName() {
      return name;
    }

    public boolean isGranted() {
      return granted;
    }
  }
}
