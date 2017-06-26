package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 15/12/2016.
 */

@Data @EqualsAndHashCode(callSuper = true) public class TimelineStats extends BaseV7Response {
  private StatusData data;

  @Data public static class StatusData {
    private long followers;
    private long following;
  }
}
