/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by neuro on 27-04-2016.
 */
public class Store {
  public static final String PUBLIC_ACCESS = "PUBLIC";
  private long id;
  private String name;
  private String avatar;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;
  private Appearance appearance;
  private Stats stats;
  @JsonProperty("links") private List<SocialChannel> socialChannels;
  private String status;
  private String access;
  private Urls urls;
  private Badge badge;

  public Badge getBadge() {
    return badge;
  }

  public void setBadge(Badge badge) {
    this.badge = badge;
  }

  public Urls getUrls() {
    return urls;
  }

  public void setUrls(Urls urls) {
    this.urls = urls;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Date getAdded() {
    return added;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Appearance getAppearance() {
    return appearance;
  }

  public void setAppearance(Appearance appearance) {
    this.appearance = appearance;
  }

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public List<SocialChannel> getSocialChannels() {
    return socialChannels;
  }

  public void setSocialChannels(List<SocialChannel> socialChannels) {
    this.socialChannels = socialChannels;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAccess() {
    return access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  public enum SocialChannelType {
    FACEBOOK, TWITTER, YOUTUBE, TWITCH
  }

  public enum BadgeType {
    NONE, BRONZE, SILVER, GOLD, PLATINUM
  }

  public static class Stats {
    private int apps;
    private int subscribers;
    private long downloads;

    public int getApps() {
      return apps;
    }

    public void setApps(int apps) {
      this.apps = apps;
    }

    public int getSubscribers() {
      return subscribers;
    }

    public void setSubscribers(int subscribers) {
      this.subscribers = subscribers;
    }

    public long getDownloads() {
      return downloads;
    }

    public void setDownloads(long downloads) {
      this.downloads = downloads;
    }
  }

  public static class Appearance {
    private String theme;
    private String description;

    public Appearance() {
    }

    public Appearance(String theme, String description) {
      this.theme = theme;
      this.description = description;
    }

    public String getTheme() {
      return theme;
    }

    public void setTheme(String theme) {
      this.theme = theme;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static class SocialChannel {
    private SocialChannelType type;
    private String name;
    private String graphic;
    private String url;

    public SocialChannelType getType() {
      return type;
    }

    public void setType(SocialChannelType type) {
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getGraphic() {
      return graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }

  public static class Urls {

    private String mobile;

    public Urls() {
    }

    public String getMobile() {
      return this.mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $mobile = this.getMobile();
      result = result * PRIME + ($mobile == null ? 43 : $mobile.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Urls)) return false;
      final Urls other = (Urls) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$mobile = this.getMobile();
      final Object other$mobile = other.getMobile();
      if (this$mobile == null ? other$mobile != null : !this$mobile.equals(other$mobile)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "Store.Urls(mobile=" + this.getMobile() + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Urls;
    }
  }

  public static class Badge {
    BadgeType name;

    public BadgeType getName() {
      return name;
    }

    public void setName(BadgeType name) {
      this.name = name;
    }
  }
}
