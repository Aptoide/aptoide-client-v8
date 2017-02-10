package cm.aptoide.pt.aptoidesdk.entities;

import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 22-11-2016.
 */
@Data public final class Media {

  private final String description;
  private final List<Screenshot> screenshots;
}
