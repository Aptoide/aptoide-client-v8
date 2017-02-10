package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

/**
 * Created by neuro on 03-11-2016.
 */
@Data public final class File {
  private final String path;
  private final String alternativePath;
  private final long size;
  private final String md5sum;
}
