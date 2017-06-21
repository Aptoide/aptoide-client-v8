package cm.aptoide.pt.dataprovider.ws.notifications;

/**
 * Created by trinkes on 7/13/16.
 */
public class GetPullNotificationsResponse {

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

  public String getAbTestingGroup() {
    return abTestingGroup;
  }

  public void setAbTestingGroup(String abTestingGroup) {
    this.abTestingGroup = abTestingGroup;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public int getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(int campaignId) {
    this.campaignId = campaignId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrlTrack() {
    return urlTrack;
  }

  public void setUrlTrack(String urlTrack) {
    this.urlTrack = urlTrack;
  }

  public Attr getAttr() {
    return attr;
  }

  public void setAttr(Attr attr) {
    this.attr = attr;
  }
}
