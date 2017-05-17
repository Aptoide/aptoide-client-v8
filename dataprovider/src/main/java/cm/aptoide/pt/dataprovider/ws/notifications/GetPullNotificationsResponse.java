package cm.aptoide.pt.dataprovider.ws.notifications;

import lombok.Data;

/**
 * Created by trinkes on 7/13/16.
 */
@Data public class GetPullNotificationsResponse {

  private String abTestingGroup;
  private String body;
  private int campaignId;
  private int type;
  private String img;
  private String lang;
  private String title;
  private String url;
  private String urlTrack;
  private Attr attr;

  @Data public static class Attr {
    String appName;
    String appGraphic;
  }
}
