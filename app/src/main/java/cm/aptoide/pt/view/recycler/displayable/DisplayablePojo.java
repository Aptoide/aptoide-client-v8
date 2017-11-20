/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;


/**
 * Created by neuro on 14-04-2016.
 */
public abstract class DisplayablePojo<T> extends Displayable {

  //private static final float REFERENCE_WIDTH_DPI = 360;
  private T pojo;

  /**
   * Needed for reflective {@link Class#newInstance()}.
   */
  public DisplayablePojo() {
  }

  public DisplayablePojo(T pojo) {
    this.pojo = pojo;
  }

  public T getPojo() {
    return this.pojo;
  }

  public DisplayablePojo<T> setPojo(T pojo) {
    this.pojo = pojo;
    return this;
  }
}
