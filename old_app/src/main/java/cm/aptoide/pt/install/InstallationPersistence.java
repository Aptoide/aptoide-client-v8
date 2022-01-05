package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomInstallation;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface InstallationPersistence {
  Observable<List<RoomInstallation>> getInstallationsHistory();

  Completable insertAll(List<RoomInstallation> roomInstallationList);

  Completable insert(RoomInstallation roomInstallation);
}
