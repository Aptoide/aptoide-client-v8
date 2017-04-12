package cm.aptoide.pt.dataprovider.ws.notifications;

import lombok.Data;

/**
 * Created by trinkes on 7/13/16.
 */
@Data public class GetPullNotificationsResponse {

  //{
  //  "ab_testing_group": "passive_promotion",
  //    "body": "Aptoide V9 available now!",
  //    "campaign_id": 1,
  //    "img": 3,
  //    "lang": "en",
  //    "title": "Aptoide V9",
  //    "url": "some_deep_url_for_aptoide_client"
  //}

  private String ab_testing_group;
  private String body;
  private String campaign_id;
  private String img;
  private String lang;
  private String title;
  private String url;

  //private List<Notification> results;
  //
  //@Data public static class Notification {
  //
  //  private Number id;
  //
  //  private String title;
  //
  //  private String message;
  //
  //  @JsonProperty("target_url") private String targetUrl;
  //
  //  @JsonProperty("track_url") private String trackUrl;
  //
  //  private Images images;
  //
  //  @Data public static class Images {
  //
  //    @JsonProperty("banner_url") private String bannerUrl;
  //    @JsonProperty("icon_url") private String iconUrl;
  //  }
  //}
}
