package cm.aptoide.pt.dataprovider.ws.v3;

/**
 * Created by danielchen on 23/10/2017.
 */

import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) public class GetPushNotificationsResponse
    extends BaseV3Response {

  private List<Notification> results;

  @Data public static class Notification {

    private int id;

    private String title;

    private String message;

    @JsonProperty("target_url") private String targetUrl;

    @JsonProperty("track_url") private String trackUrl;

    private Images images;

    @Data public static class Images {

      @JsonProperty("banner_url") private String bannerUrl;
      @JsonProperty("icon_url") private String iconUrl;
    }
  }
}