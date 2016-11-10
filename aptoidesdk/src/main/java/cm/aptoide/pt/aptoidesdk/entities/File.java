package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.GetAppMeta;
import lombok.Data;

/**
 * Created by neuro on 03-11-2016.
 */
@Data public class File {
  private final String path;
  private final String alternativePath;
  private final long size;

  public static File from(GetAppMeta.GetAppMetaFile file) {

    String path = file.getPath();
    String alternativePath = file.getPathAlt();
    long size = file.getFilesize();

    return new File(path, alternativePath, size);
  }
}
