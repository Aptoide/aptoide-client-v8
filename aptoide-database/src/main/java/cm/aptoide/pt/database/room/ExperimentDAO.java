package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface ExperimentDAO {

  @Query("SELECT * FROM experiment where experimentName LIKE :identifier")
  Observable<RoomExperiment> get(String identifier);

  @Insert(onConflict = REPLACE) void save(RoomExperiment experiment);
}
