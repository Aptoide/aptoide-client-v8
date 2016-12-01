package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.aptoidesdk.misc.Orientation;
import lombok.Data;

/**
 * Created by neuro on 01-12-2016.
 */
@Data public final class Screenshot {

  private final String url;
  private final int height;
  private final int width;

  public Orientation getOrientation() {
    return Orientation.getOrientation(height, width);
  }
}
