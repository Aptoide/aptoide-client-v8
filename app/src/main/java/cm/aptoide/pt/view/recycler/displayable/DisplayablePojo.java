/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;

import cm.aptoide.pt.annotation.Ignore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 14-04-2016.
 */
@Ignore @Accessors(chain = true) public abstract class DisplayablePojo<T> extends Displayable {

  //private static final float REFERENCE_WIDTH_DPI = 360;
  @Getter @Setter private T pojo;

  /**
   * Needed for reflective {@link Class#newInstance()}.
   */
  public DisplayablePojo() {
  }

  public DisplayablePojo(T pojo) {
    this.pojo = pojo;
  }
}
