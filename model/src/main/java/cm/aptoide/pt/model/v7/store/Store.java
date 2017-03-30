/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.model.v7.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 27-04-2016.
 */
@Data @Accessors(chain = true) public class Store {
  private long id;
  private String name;
  private String avatar;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;
  private Appearance appearance;
  private Stats stats;
  @JsonProperty("links") private List<SocialChannel> socialChannels;

  public enum SocialChannelType {
    FACEBOOK, TWITTER, YOUTUBE, TWITCH
  }

  @Data public static class Stats {
    private int apps;
    private int subscribers;
    private int downloads;
  }

  @Data public static class Appearance {
    private String theme;
    private String description;

    public Appearance() {
    }

    public Appearance(String theme, String description) {
      this.theme = theme;
      this.description = description;
    }
  }

  @Data public static class SocialChannel {
    private SocialChannelType type;
    private String name;
    private String graphic;
    private String url;
  }
}
