/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing;

public class PaymentService {

  private final String id;
  private final String type;
  private final String name;
  private final String description;
  private final String icon;

  public PaymentService(String id, String type, String name, String description, String icon) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
    this.icon = icon;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public String getIcon() {
    return icon;
  }
}