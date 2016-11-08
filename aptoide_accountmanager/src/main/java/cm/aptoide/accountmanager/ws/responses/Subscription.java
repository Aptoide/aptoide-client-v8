/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import lombok.Data;

/**
 * Created by sithengineer on 27/06/16.
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
