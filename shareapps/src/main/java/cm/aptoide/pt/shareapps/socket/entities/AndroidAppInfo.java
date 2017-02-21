package cm.aptoide.pt.shareapps.socket.entities;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 27-01-2017.
 */
@Data public class AndroidAppInfo implements Serializable {

  private FileInfo apk, mainObb, patchObb;
  private String appName;
  private String packageName;
  private String filePath;
  private String obbsFilePath;

  public AndroidAppInfo(String appName, String packageName, String filePath, String obbsFilePath, File apk) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.obbsFilePath = obbsFilePath;
    this.apk = new FileInfo(apk);
  }

  public AndroidAppInfo(String appName, String packageName, String filePath,File apk) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.apk = new FileInfo(apk);
  }


  public AndroidAppInfo(File apk, File mainObb, File patchObb) {
    this(apk, mainObb);
    this.patchObb = new FileInfo(patchObb);
  }

  public AndroidAppInfo(File apk, File mainObb) {
    this(apk);
    this.mainObb = new FileInfo(mainObb);
  }

  public AndroidAppInfo(File apk) {
    this.apk = new FileInfo(apk);
  }

  //public String getAppName(){
  //  return this.appName;
  //}
  //
  //public String getPackageName(){
  //  return this.packageName;
  //}
  //
  //public String getFilePath(){
  //  return this.filePath;
  //}
  //
  //public String getObbsFilePath(){
  //  if(obbsFilePath!=null && !obbsFilePath.isEmpty()){
  //    return this.getObbsFilePath();
  //  }else{
  //    return "noObbs";
  //  }
  //}

  public List<String> getFilesPathsList() {
    List<String> list = new LinkedList<>();
    list.add(apk.getFilePath());

    if (hasMainObb()) {
      list.add(getMainObb().getFilePath());
    }

    if (hasPatchObb()) {
      list.add(getPatchObb().getFilePath());
    }

    return list;
  }

  public boolean hasMainObb() {
    return mainObb != null;
  }

  public boolean hasPatchObb() {
    return patchObb != null;
  }

  public List<FileInfo> getFileInfosList() {
    List<FileInfo> list = new LinkedList<>();
    list.add(apk);

    if (hasMainObb()) {
      list.add(getMainObb());
    }

    if (hasPatchObb()) {
      list.add(getPatchObb());
    }

    return list;
  }

  public List<FileInfo> getFiles() {
    List<FileInfo> fileInfos = new LinkedList<>();

    fileInfos.add(apk);

    if (hasMainObb()) {
      fileInfos.add(mainObb);
    }

    if (hasMainObb()) {
      fileInfos.add(patchObb);
    }

    return fileInfos;
  }

  public long getFilesSize() {
    long total = apk.getSize();

    if (hasMainObb()) {
      total += mainObb.getSize();
    }

    if (hasPatchObb()) {
      total += patchObb.getSize();
    }

    return total;
  }
}
