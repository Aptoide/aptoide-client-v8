/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

/**
 * Created by neuro on 22-04-2016.
 */
@Data public class Group {

  private long id;
  private String name;
  private String title;
  private String icon;
  private String graphic;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date modified;
  private Parent parent;
  private Stats stats;

  @Data public static class Parent {

    private long id;
    private String name;
    private String title;
    private String icon;
    private String graphic;
  }

  @Data public static class Stats {

    private int groups;
    private int items;
  }
}
