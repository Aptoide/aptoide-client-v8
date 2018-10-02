package cm.aptoide.pt.dataprovider.model.v7.discovery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by franciscocalado on 27/09/2018.
 */

public class VideosData {

  private String type;
  private String url;
  private String thumbnail;
  private VideoApp app;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date date;
  private String source;
  private long currentViewers;
  private long totalViewers;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public VideoApp getApp() {
    return app;
  }

  public void setApp(VideoApp app) {
    this.app = app;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public long getCurrentViewers() {
    return currentViewers;
  }

  public void setCurrentViewers(long currentViewers) {
    this.currentViewers = currentViewers;
  }

  public long getTotalViewers() {
    return totalViewers;
  }

  public void setTotalViewers(long totalViewers) {
    this.totalViewers = totalViewers;
  }

  public class VideoApp {
    private VideoAppStats stats;
    private String id;
    private String name;
    @JsonProperty("package") private String packageName;
    private String uname;
    private long size;
    private String icon;
    private String graphic;

    public VideoAppStats getStats() {
      return stats;
    }

    public void setStats(VideoAppStats stats) {
      this.stats = stats;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getUname() {
      return uname;
    }

    public void setUname(String uname) {
      this.uname = uname;
    }

    public long getSize() {
      return size;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getGraphic() {
      return graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
    }

    public class VideoAppStats {
      private VideoRating rating;

      public VideoRating getRating() {
        return rating;
      }

      public void setRating(VideoRating rating) {
        this.rating = rating;
      }

      public class VideoRating {
        private float avg;
        private double total;

        public float getAvg() {
          return avg;
        }

        public void setAvg(float avg) {
          this.avg = avg;
        }

        public double getTotal() {
          return total;
        }

        public void setTotal(double total) {
          this.total = total;
        }
      }
    }
  }
}
