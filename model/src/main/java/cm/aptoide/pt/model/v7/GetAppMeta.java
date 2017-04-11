/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/08/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetAppMeta extends BaseV7Response {

  private App data;

  @Data public static class App {

    private long id;
    private String name;
    @JsonProperty("package") private String packageName;
    private long size;
    private String icon;
    private String graphic;
    private String added;
    private String modified;
    private Developer developer;
    private Store store;
    private GetAppMetaFile file;
    private Media media;
    private Urls urls;
    private Stats stats;
    private Obb obb;
    private Pay pay;

    public boolean isPaid() {
      return (pay != null && pay.getPrice() > 0.0f);
    }

    public String getMd5() {
      return file == null ? "" : file.getMd5sum();
    }
  }

  @Data public static class Pay {

    private int productId;
    private double price;
    private String currency;
    private String symbol;
    private double taxRate;
    private String status;

    public boolean isPaid() {
      return "OK".equalsIgnoreCase(status);
    }

    public void setPaid() {
      status = "OK";
    }
  }

  @Data public static class Developer {

    private String name;
    private String website;
    private String email;
    private String privacy;
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class GetAppMetaFile extends File {

    private GetAppMetaFile.Signature signature;
    private GetAppMetaFile.Hardware hardware;
    private Malware malware;
    private GetAppMetaFile.Flags flags;
    private List<String> usedFeatures;
    private List<String> usedPermissions;
    private List<String> tags;

    public boolean isGoodApp() {
      return this.flags != null && flags.review != null && flags.review.equalsIgnoreCase(
          Flags.GOOD);
    }

    @Data public static class Signature {

      private String sha1;
      private String owner;
    }

    @Data public static class Hardware {

      private int sdk;
      private String screen;
      private int gles;
      private List<String> cpus;
      /**
       * Second array contains only two values: First value is the screen, second value is
       * the
       * density
       */
      private List<List<Integer>> densities;
    }

    @Data public static class Flags {

      public static final String GOOD = "GOOD";
      /**
       * When there's a review, there are no votes
       * <p>
       * flags: { review": "GOOD" },
       */
      public String review;
      private List<GetAppMetaFile.Flags.Vote> votes;

      @Data public static class Vote {

        /**
         * type can be:
         * <p>
         * FAKE, FREEZE, GOOD, LICENSE, VIRUS
         */
        private GetAppMetaFile.Flags.Vote.Type type;
        private int count;

        public enum Type {
          FAKE, FREEZE, GOOD, LICENSE, VIRUS
        }
      }
    }
  }

  @Data public static class Media {

    private List<String> keywords;
    private String description;
    private String news;
    private List<Media.Screenshot> screenshots;
    private List<Media.Video> videos;

    @Data public static class Video {

      private String type;
      private String url;
      private String thumbnail;
    }

    @Data public static class Screenshot {

      private String url;
      private int height;
      private int width;

      public String getOrientation() {
        return height > width ? "portrait" : "landscape";
      }
    }
  }

  @Data public static class Urls {

    private String w;
    private String m;
  }

  @Data public static class Stats {

    private Stats.Rating rating;
    private int downloads;
    private int pdownloads;

    @Data public static class Rating {

      private float avg;
      private int total;
      private List<Stats.Rating.Vote> votes;

      @Data public static class Vote {

        private int value;
        private int count;
      }
    }
  }
}