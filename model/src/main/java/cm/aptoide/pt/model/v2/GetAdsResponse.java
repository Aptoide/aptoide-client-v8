/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetAppMeta;
import lombok.EqualsAndHashCode;

/**
 * GetAdsResponse.
 */
@lombok.Data @EqualsAndHashCode(callSuper = true) public class GetAdsResponse extends BaseV7Response{

  @JsonProperty("datalist") private DataList dataList;

  @lombok.Data public static class DataList{
    private long total;
    private int count;
    private int offset;
    private int limit;
    private int next;
    private int hidden;
    private boolean loaded;
    private List<Ad> list;
  }

  @lombok.Data public static class Ad{
    private long id;
    private String name;
    private String label;
    private String type;
    private String model;
    private double payout;
    private String keyword;
    private Campaign campaign;
    private Network network;
    private Urls urls;
    private Data data;
  }

  @lombok.Data public static class Campaign{
    private long id;
    private String name;
    private String label;
  }

  @lombok.Data public static class Network{
    private int id;
    private String name;
  }

  @lombok.Data public static class Urls{
    private List<String> referrers;
    private List<String> impressions;
    private List<String> clicks;
    private List<String> downloads;
    private List<String> installs;
  }

  @lombok.Data public static class Data{
    private App app;
  }

  @lombok.Data public static class App{
    private long id;
    private String name;
    @JsonProperty ("package") private String packageName;
    private long size;
    private String icon;
    private String added;
    private String modified;
    private String updated;
    private Store store;
    private File file;
    private Stats stats;
  }

  @lombok.Data public static class Store{
    private long id;
    private String name;
  }

  @lombok.Data public static class File{
    private String vername;
    private String vercode;
    private String md5sum;
  }

  @lombok.Data public static class Stats{
    private long downloads;
    private long pdownloads;
    private Rating rating;
    private PRating prating;
  }

  @lombok.Data public static class Rating{
    private double avg;
    private int total;
  }

  @lombok.Data public static class PRating{
    private double avg;
    private int total;
  }
}

