package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadAppFile {

  private String mainDownloadPath;
  private String alternativeDownloadPath;
  private String downloadMd5;
  private int versionCode;
  private String packageName;
  private String fileName;
  private FileType fileType;
  private int fileSize;

  public DownloadAppFile(String mainDownloadPath, String alternativeDownloadPath,
      String downloadMd5, int versionCode, String packageName, String fileName, FileType fileType,
      int fileSize) {
    this.mainDownloadPath = mainDownloadPath;
    this.alternativeDownloadPath = alternativeDownloadPath;
    this.downloadMd5 = downloadMd5;
    this.versionCode = versionCode;
    this.packageName = packageName;
    this.fileName = fileName;
    this.fileType = fileType;
    this.fileSize = fileSize;
  }

  public String getMainDownloadPath() {
    return mainDownloadPath;
  }

  public String getAlternativeDownloadPath() {
    return alternativeDownloadPath;
  }

  public String getDownloadMd5() {
    return downloadMd5;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getFileType() {
    return fileType.getType();
  }

  public String getFileName() {
    return fileName;
  }

  public int getFileSize() {
    return fileSize;
  }

  public enum FileType {
    APK(0), OBB_MAIN(1), OBB_PATCH(2), GENERIC(3);

    private final int type;

    FileType(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
  }
}
