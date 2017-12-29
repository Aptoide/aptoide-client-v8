package cm.aptoide.pt.dataprovider.ws.v3;

/**
 * Created by danielchen on 23/10/2017.
 */

import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GetPushNotificationsResponse extends BaseV3Response {

  private List<Notification> results;

  public GetPushNotificationsResponse() {
  }

  public List<Notification> getResults() {
    return this.results;
  }

  public void setResults(List<Notification> results) {
    this.results = results;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $results = this.getResults();
    result = result * PRIME + ($results == null ? 43 : $results.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetPushNotificationsResponse)) return false;
    final GetPushNotificationsResponse other = (GetPushNotificationsResponse) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$results = this.getResults();
    final Object other$results = other.getResults();
    if (this$results == null ? other$results != null : !this$results.equals(other$results)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "GetPushNotificationsResponse(results=" + this.getResults() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetPushNotificationsResponse;
  }

  public static class Notification {

    private int id;

    private String title;

    private String message;

    @JsonProperty("target_url") private String targetUrl;

    @JsonProperty("track_url") private String trackUrl;

    private Images images;

    public Notification() {
    }

    public int getId() {
      return this.id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getTitle() {
      return this.title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getMessage() {
      return this.message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getTargetUrl() {
      return this.targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
      this.targetUrl = targetUrl;
    }

    public String getTrackUrl() {
      return this.trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
      this.trackUrl = trackUrl;
    }

    public Images getImages() {
      return this.images;
    }

    public void setImages(Images images) {
      this.images = images;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + this.getId();
      final Object $title = this.getTitle();
      result = result * PRIME + ($title == null ? 43 : $title.hashCode());
      final Object $message = this.getMessage();
      result = result * PRIME + ($message == null ? 43 : $message.hashCode());
      final Object $targetUrl = this.getTargetUrl();
      result = result * PRIME + ($targetUrl == null ? 43 : $targetUrl.hashCode());
      final Object $trackUrl = this.getTrackUrl();
      result = result * PRIME + ($trackUrl == null ? 43 : $trackUrl.hashCode());
      final Object $images = this.getImages();
      result = result * PRIME + ($images == null ? 43 : $images.hashCode());
      return result;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Notification;
    }

    public static class Images {

      @JsonProperty("banner_url") private String bannerUrl;
      @JsonProperty("icon_url") private String iconUrl;

      public Images() {
      }

      public String getBannerUrl() {
        return this.bannerUrl;
      }

      public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
      }

      public String getIconUrl() {
        return this.iconUrl;
      }

      public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Images;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Images)) return false;
        final Images other = (Images) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$bannerUrl = this.getBannerUrl();
        final Object other$bannerUrl = other.getBannerUrl();
        if (this$bannerUrl == null ? other$bannerUrl != null
            : !this$bannerUrl.equals(other$bannerUrl)) {
          return false;
        }
        final Object this$iconUrl = this.getIconUrl();
        final Object other$iconUrl = other.getIconUrl();
        if (this$iconUrl == null ? other$iconUrl != null : !this$iconUrl.equals(other$iconUrl)) {
          return false;
        }
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $bannerUrl = this.getBannerUrl();
        result = result * PRIME + ($bannerUrl == null ? 43 : $bannerUrl.hashCode());
        final Object $iconUrl = this.getIconUrl();
        result = result * PRIME + ($iconUrl == null ? 43 : $iconUrl.hashCode());
        return result;
      }

      public String toString() {
        return "GetPushNotificationsResponse.Notification.Images(bannerUrl="
            + this.getBannerUrl()
            + ", iconUrl="
            + this.getIconUrl()
            + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Notification)) return false;
      final Notification other = (Notification) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$title = this.getTitle();
      final Object other$title = other.getTitle();
      if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
      final Object this$message = this.getMessage();
      final Object other$message = other.getMessage();
      if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
        return false;
      }
      final Object this$targetUrl = this.getTargetUrl();
      final Object other$targetUrl = other.getTargetUrl();
      if (this$targetUrl == null ? other$targetUrl != null
          : !this$targetUrl.equals(other$targetUrl)) {
        return false;
      }
      final Object this$trackUrl = this.getTrackUrl();
      final Object other$trackUrl = other.getTrackUrl();
      if (this$trackUrl == null ? other$trackUrl != null : !this$trackUrl.equals(other$trackUrl)) {
        return false;
      }
      final Object this$images = this.getImages();
      final Object other$images = other.getImages();
      if (this$images == null ? other$images != null : !this$images.equals(other$images)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "GetPushNotificationsResponse.Notification(id="
          + this.getId()
          + ", title="
          + this.getTitle()
          + ", message="
          + this.getMessage()
          + ", targetUrl="
          + this.getTargetUrl()
          + ", trackUrl="
          + this.getTrackUrl()
          + ", images="
          + this.getImages()
          + ")";
    }
  }
}
