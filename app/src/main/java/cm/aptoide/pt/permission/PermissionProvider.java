package cm.aptoide.pt.permission;

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

    @Override public int hashCode() {
      int result = requestCode;
      result = 31 * result + name.hashCode();
      return result;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Permission that = (Permission) o;

      if (requestCode != that.requestCode) return false;
      return name.equals(that.name);
    }
  }
}
