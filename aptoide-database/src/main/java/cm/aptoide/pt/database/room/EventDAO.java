package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface EventDAO {

  @Query("SELECT * FROM event") Observable<List<RoomEvent>> getAll();

  @Insert(onConflict = REPLACE) void insert(RoomEvent roomEvent);

  @Delete void delete(RoomEvent roomEvent);
}

