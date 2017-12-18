package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pedroribeiro on 28/06/17.
 */

public class BiUtmAnalyticsRequestBody extends BaseBody {

  private final Data data;

  public BiUtmAnalyticsRequestBody(Data data) {
    this.data = data;
  }

  public Data getData() {
    return this.data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof BiUtmAnalyticsRequestBody;
  }

  public static class Data {
    private String entryPoint;
    private String siteVersion;
    private App app;
    private UTM utm;
    private String userAgent;

    public Data() {
    }

    public String getEntryPoint() {
      return this.entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
      this.entryPoint = entryPoint;
    }

    public String getSiteVersion() {
      return this.siteVersion;
    }

    public void setSiteVersion(String siteVersion) {
      this.siteVersion = siteVersion;
    }

    public App getApp() {
      return this.app;
    }

    public void setApp(App app) {
      this.app = app;
    }

    public UTM getUtm() {
      return this.utm;
    }

    public void setUtm(UTM utm) {
      this.utm = utm;
    }

    public String getUserAgent() {
      return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Data;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Data)) return false;
      final Data other = (Data) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$entryPoint = this.getEntryPoint();
      final Object other$entryPoint = other.getEntryPoint();
      if (this$entryPoint == null ? other$entryPoint != null
          : !this$entryPoint.equals(other$entryPoint)) {
        return false;
      }
      final Object this$siteVersion = this.getSiteVersion();
      final Object other$siteVersion = other.getSiteVersion();
      if (this$siteVersion == null ? other$siteVersion != null
          : !this$siteVersion.equals(other$siteVersion)) {
        return false;
      }
      final Object this$app = this.getApp();
      final Object other$app = other.getApp();
      if (this$app == null ? other$app != null : !this$app.equals(other$app)) return false;
      final Object this$utm = this.getUtm();
      final Object other$utm = other.getUtm();
      if (this$utm == null ? other$utm != null : !this$utm.equals(other$utm)) return false;
      final Object this$userAgent = this.getUserAgent();
      final Object other$userAgent = other.getUserAgent();
      if (this$userAgent == null ? other$userAgent != null
          : !this$userAgent.equals(other$userAgent)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $entryPoint = this.getEntryPoint();
      result = result * PRIME + ($entryPoint == null ? 43 : $entryPoint.hashCode());
      final Object $siteVersion = this.getSiteVersion();
      result = result * PRIME + ($siteVersion == null ? 43 : $siteVersion.hashCode());
      final Object $app = this.getApp();
      result = result * PRIME + ($app == null ? 43 : $app.hashCode());
      final Object $utm = this.getUtm();
      result = result * PRIME + ($utm == null ? 43 : $utm.hashCode());
      final Object $userAgent = this.getUserAgent();
      result = result * PRIME + ($userAgent == null ? 43 : $userAgent.hashCode());
      return result;
    }

    public String toString() {
      return "BiUtmAnalyticsRequestBody.Data(entryPoint="
          + this.getEntryPoint()
          + ", siteVersion="
          + this.getSiteVersion()
          + ", app="
          + this.getApp()
          + ", utm="
          + this.getUtm()
          + ", userAgent="
          + this.getUserAgent()
          + ")";
    }
  }

  public static class App {
    private String url;
    @JsonProperty("package") private String packageName;

    public App() {
    }

    public String getUrl() {
      return this.url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getPackageName() {
      return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    protected boolean canEqual(Object other) {
      return other instanceof App;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof App)) return false;
      final App other = (App) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$url = this.getUrl();
      final Object other$url = other.getUrl();
      if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
      final Object this$packageName = this.getPackageName();
      final Object other$packageName = other.getPackageName();
      if (this$packageName == null ? other$packageName != null
          : !this$packageName.equals(other$packageName)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $url = this.getUrl();
      result = result * PRIME + ($url == null ? 43 : $url.hashCode());
      final Object $packageName = this.getPackageName();
      result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
      return result;
    }

    public String toString() {
      return "BiUtmAnalyticsRequestBody.App(url="
          + this.getUrl()
          + ", packageName="
          + this.getPackageName()
          + ")";
    }
  }

  public static class UTM {
    private String source;
    private String medium;
    private String campaign;
    private String content;

    public UTM() {
    }

    public String getSource() {
      return this.source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getMedium() {
      return this.medium;
    }

    public void setMedium(String medium) {
      this.medium = medium;
    }

    public String getCampaign() {
      return this.campaign;
    }

    public void setCampaign(String campaign) {
      this.campaign = campaign;
    }

    public String getContent() {
      return this.content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    protected boolean canEqual(Object other) {
      return other instanceof UTM;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof UTM)) return false;
      final UTM other = (UTM) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$source = this.getSource();
      final Object other$source = other.getSource();
      if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
        return false;
      }
      final Object this$medium = this.getMedium();
      final Object other$medium = other.getMedium();
      if (this$medium == null ? other$medium != null : !this$medium.equals(other$medium)) {
        return false;
      }
      final Object this$campaign = this.getCampaign();
      final Object other$campaign = other.getCampaign();
      if (this$campaign == null ? other$campaign != null : !this$campaign.equals(other$campaign)) {
        return false;
      }
      final Object this$content = this.getContent();
      final Object other$content = other.getContent();
      if (this$content == null ? other$content != null : !this$content.equals(other$content)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $source = this.getSource();
      result = result * PRIME + ($source == null ? 43 : $source.hashCode());
      final Object $medium = this.getMedium();
      result = result * PRIME + ($medium == null ? 43 : $medium.hashCode());
      final Object $campaign = this.getCampaign();
      result = result * PRIME + ($campaign == null ? 43 : $campaign.hashCode());
      final Object $content = this.getContent();
      result = result * PRIME + ($content == null ? 43 : $content.hashCode());
      return result;
    }

    public String toString() {
      return "BiUtmAnalyticsRequestBody.UTM(source="
          + this.getSource()
          + ", medium="
          + this.getMedium()
          + ", campaign="
          + this.getCampaign()
          + ", content="
          + this.getContent()
          + ")";
    }
  }  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BiUtmAnalyticsRequestBody)) return false;
    final BiUtmAnalyticsRequestBody other = (BiUtmAnalyticsRequestBody) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }



  public String toString() {
    return "BiUtmAnalyticsRequestBody(data=" + this.getData() + ")";
  }
}
