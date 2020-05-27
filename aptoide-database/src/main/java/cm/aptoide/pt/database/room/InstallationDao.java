package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface InstallationDao {

  @Query("SELECT * FROM installation") Observable<List<RoomInstallation>> getAll();

  @Insert(onConflict = REPLACE) void insertAll(List<RoomInstallation> roomInstallationList);

  @Insert(onConflict = REPLACE) void insert(RoomInstallation roomInstallation);
}
