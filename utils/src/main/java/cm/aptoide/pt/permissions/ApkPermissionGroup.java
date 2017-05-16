/*
 * Copyright (c) 2016.
 * Modified on 21/07/2016.
 */

package cm.aptoide.pt.permissions;

import java.util.ArrayList;
import java.util.List;

public class ApkPermissionGroup {

  private String name;
  private List<String> descriptions = new ArrayList<>();

  public ApkPermissionGroup(String name, String description) {
    this.name = name;
    this.descriptions.add(description);
  }

  public List<String> getDescriptions() {
    return descriptions;
  }

  public void setDescription(String description) {
    this.descriptions.add(description);
  }

  /**
   * The contains method was overridden to check if the object contains the same permissions size
   * and name. Could be refactored to check if the permissions'
   * list is the same.
   */
  @Override public boolean equals(Object o) {
    return o instanceof ApkPermissionGroup
        && this.getName()
        .equals(((ApkPermissionGroup) o).getName())
        && this.descriptions.size() == ((ApkPermissionGroup) o).descriptions.size();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

