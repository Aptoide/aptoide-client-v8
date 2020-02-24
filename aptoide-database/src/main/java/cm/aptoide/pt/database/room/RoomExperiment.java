package cm.aptoide.pt.database.room;

import androidx.room.Entity;
import io.realm.annotations.PrimaryKey;

@Entity(tableName = "experiment") public class RoomExperiment {

  public static String PRIMARY_KEY_NAME = "experimentName";

  @PrimaryKey private String experimentName;
  private long requestTime;
  private String assignment;
  private String payload;
  private boolean partOfExperiment;
  private boolean experimentOver;

  public RoomExperiment(String experimentName, long requestTime, String assignment, String payload,
      boolean partOfExperiment, boolean experimentOver) {
    this.experimentName = experimentName;
    this.requestTime = requestTime;
    this.assignment = assignment;
    this.payload = payload;
    this.partOfExperiment = partOfExperiment;
    this.experimentOver = experimentOver;
  }

  public String getExperimentName() {
    return experimentName;
  }

  public long getRequestTime() {
    return requestTime;
  }

  public String getAssignment() {
    return assignment;
  }

  public String getPayload() {
    return payload;
  }

  public boolean isPartOfExperiment() {
    return partOfExperiment;
  }

  public boolean isExperimentOver() {
    return experimentOver;
  }
}
