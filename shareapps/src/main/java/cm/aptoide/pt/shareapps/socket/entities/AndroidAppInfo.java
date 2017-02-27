package cm.aptoide.pt.shareapps.socket.entities;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 27-01-2017.
 */
@Data public class AndroidAppInfo implements Serializable {

  private FileInfo apk, mainObb, patchObb;
  private String appName;
  private String packageName;
  private List<FileInfo> fileInfos;

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb, File patchObb) {
    this(appName, packageName, apk, mainObb);
    this.patchObb = new FileInfo(patchObb);
  }

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb) {
    this(appName, packageName, apk);
    this.mainObb = new FileInfo(mainObb);
  }

  public AndroidAppInfo(String appName, String packageName, File apk) {
    this.appName = appName;
    this.packageName = packageName;
    this.apk = new FileInfo(apk);
  }

  public AndroidAppInfo(String appName, String packageName, List<FileInfo> fileInfos) {
    this.fileInfos = fileInfos;
    this.packageName = packageName;
    this.appName = appName;
    this.apk = extractApk();
  }

  public boolean hasMainObb() {
    return mainObb != null;
  }

  public boolean hasPatchObb() {
    return patchObb != null;
  }


  public List<FileInfo> getFiles() {
    //List<FileInfo> fileInfos = new LinkedList<>();
    //
    //fileInfos.add(apk);
    //
    //if (hasMainObb()) {
    //  fileInfos.add(mainObb);
    //}
    //
    //if (hasMainObb()) {
    //  fileInfos.add(patchObb);
    //}

    return fileInfos;
  }

  public long getFilesSize() {
    //long total = apk.getSize();
    //
    //if (hasMainObb()) {
    //  total += mainObb.getSize();
    //}
    //
    //if (hasPatchObb()) {
    //  total += patchObb.getSize();
    //}
    //
    //return total;
    long total=0;
    if(fileInfos!=null){
      for(int i=0;i<fileInfos.size();i++){
        total += fileInfos.get(i).getSize();
      }
    }
    return total;
  }

  private FileInfo extractApk(){
    for(int i=0;i<fileInfos.size();i++){
      if(fileInfos.get(i).getFilePath().endsWith(".apk")){
        return fileInfos.get(i);
      }
    }
    return null;
  }
}
