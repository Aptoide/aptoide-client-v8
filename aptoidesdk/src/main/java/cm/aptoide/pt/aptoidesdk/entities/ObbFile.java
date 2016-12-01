package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

/**
 * Created by neuro on 01-12-2016.
 */
@Data public final class ObbFile {

  private final String path;
  private final String md5sum;
  private final String filename;
  private final long filesize;
}
