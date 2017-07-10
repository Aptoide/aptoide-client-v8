package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 27-01-2017.
 */
@Data @Accessors(chain = true) public class AndroidAppInfo implements Serializable {

  private final String appName;
  private final String packageName;
  private final List<FileInfo> fileInfos;
  private FileInfo apk, mainObb, patchObb;
  private String senderName;
  private byte[] icon;

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb, File patchObb,
      byte[] icon) {
    this.appName = appName;
    this.packageName = packageName;
    this.apk = new FileInfo(apk);
    this.mainObb = new FileInfo(mainObb);
    this.patchObb = new FileInfo(patchObb);
    this.icon = icon;

    fileInfos = buildFileInfos();
  }

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb) {
    this(appName, packageName, apk, mainObb, null, null);
  }

  public AndroidAppInfo(String appName, String packageName, File apk) {
    this(appName, packageName, apk, null, null, null);
  }

  @Deprecated public AndroidAppInfo(String appName, String packageName, List<FileInfo> fileInfos) {
    this.fileInfos = fileInfos;
    this.packageName = packageName;
    this.appName = appName;
    this.apk = extractApk();
  }

  private FileInfo extractApk() {
    for (int i = 0; i < fileInfos.size(); i++) {
      if (fileInfos.get(i)
          .getFilePath()
          .endsWith(".apk")) {
        return fileInfos.get(i);
      }
    }
    return null;
  }

  private List<FileInfo> buildFileInfos() {
    List<FileInfo> fileInfos = new LinkedList<>();

    fileInfos.add(apk);
    fileInfos.add(mainObb);
    fileInfos.add(patchObb);

    return Collections.unmodifiableList(fileInfos);
  }

  public long getFilesSize() {
    long total = 0;
    if (fileInfos != null) {
      for (int i = 0; i < fileInfos.size(); i++) {
        total += fileInfos.get(i)
            .getSize();
      }
    }
    return total;
  }
}
