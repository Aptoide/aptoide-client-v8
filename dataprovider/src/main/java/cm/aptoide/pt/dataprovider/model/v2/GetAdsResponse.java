/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.model.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * GetAdsResponse.
 */
@lombok.Data public class GetAdsResponse {

  private List<Ad> ads;
  private Options options;

  @lombok.Data public static class Data {

    private long id;
    private String name;
    private String repo;
    @JsonProperty("package") private String packageName;
    private String md5sum;
    private long size;
    private int vercode;
    private String vername;
    private String icon;
    private int downloads;
    private int stars;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date added;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date modified;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date updated;
  }

  @lombok.Data public static class Ad {

    private Data data;
    private Info info;
    private Partner partner;
    private Partner tracker;
  }

  @lombok.Data public static class Info {

    private long adId;
    private String adType;
    private String cpcUrl;
    private String cpiUrl;
    private String cpdUrl;
  }

  @lombok.Data public static class Partner {

    private Info info;
    private Data data;

    @lombok.Data public static class Info {

      private int id;
      private String name;
    }

    @lombok.Data public static class Data {

      private String clickUrl;
      private String impressionUrl;
    }
  }

  @lombok.Data public static class Options {

    private Boolean mediation = true;
  }
}
