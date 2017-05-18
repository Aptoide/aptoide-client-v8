/*
 * Copyright (c) 2016.
 * Modified on 27/06/2016.
 */

package cm.aptoide.pt.model.v3;

import lombok.Data;

/**
 * Created on 27/06/16.
 */
@Data public class Subscription {

  private Number id;
  private String name;
  private String avatar;
  private String downloads;
  private String theme;
  private String description;
  private String items;
  private String view;
  private String avatarHd;
}
