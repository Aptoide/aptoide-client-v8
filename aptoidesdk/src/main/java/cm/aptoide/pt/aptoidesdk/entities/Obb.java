/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

@Data public final class Obb {

  private final ObbFile main;
  private final ObbFile patch;
}
