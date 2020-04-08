package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "aptoideinstallapp") public class RoomAptoideInstallApp {

  @PrimaryKey @NonNull private String packageName;

  public RoomAptoideInstallApp(@NonNull String packageName) {
    this.packageName = packageName;
  }

  @NonNull public String getPackageName() {
    return packageName;
  }
}
