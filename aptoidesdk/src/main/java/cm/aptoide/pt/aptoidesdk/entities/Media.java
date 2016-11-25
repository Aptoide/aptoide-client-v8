package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.aptoidesdk.misc.Orientation;
import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 22-11-2016.
 */
@Data public final class Media {

  private final String description;
  private final List<Screenshot> screenshots;

  @Data public static final class Screenshot {

    private final String url;
    private final int height;
    private final int width;

    public Orientation getOrientation() {
      return Orientation.getOrientation(height, width);
    }
  }
}
