/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by neuro on 20-04-2016.
 */
//@Data
public abstract class BaseV7EndlessResponse extends BaseV7Response {

  protected static final int NEXT_STEP = 10;

  private final boolean stableTotal;

  public BaseV7EndlessResponse() {
    this(true);
  }

  public BaseV7EndlessResponse(boolean stableTotal) {
    this.stableTotal = stableTotal;
  }

  public abstract int getTotal();

  public abstract int getNextSize();

  public abstract boolean hasData();

  public boolean hasStableTotal() {
    return stableTotal;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    result = result * PRIME + (this.stableTotal ? 79 : 97);
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BaseV7EndlessResponse)) return false;
    final BaseV7EndlessResponse other = (BaseV7EndlessResponse) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    if (this.stableTotal != other.stableTotal) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof BaseV7EndlessResponse;
  }
}
