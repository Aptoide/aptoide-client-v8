package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomInstalled;
import java.util.List;
import rx.Observable;

public interface InstalledPersistence {

  Observable<List<RoomInstalled>> getAllInstalled();
}
