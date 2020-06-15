package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface ExperimentDAO {

  @Query("SELECT * FROM experiment WHERE experimentName = :identifier") Single<RoomExperiment> get(
      String identifier);

  @Insert(onConflict = REPLACE) void save(RoomExperiment experiment);
}
