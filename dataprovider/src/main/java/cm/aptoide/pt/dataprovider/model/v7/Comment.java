/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

/**
 * Created by neuro on 04-07-2016.
 */
@Data public class Comment {

  private long id;
  private String body;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  private User user;
  private Long parentReview;
  private Parent parent;

  public enum Access {
    PUBLIC, PRIVATE, UNLISTED
  }

  @Data public static class User {
    private long id;
    private String name;
    private String avatar;
    private Access access;
  }

  @Data public static class Parent {
    private long id;
  }
}
