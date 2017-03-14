package cm.aptoide.pt.spotandshare.socket.util;

import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import java.util.List;

/**
 * Created by neuro on 21-02-2017.
 */

public class FileInfoUtils {

  public static long computeTotalSize(List<FileInfo> fileInfos) {
    long total = 0;
    for (FileInfo fileInfo : fileInfos) {
      total += fileInfo.getSize();
    }

    return total;
  }
}
