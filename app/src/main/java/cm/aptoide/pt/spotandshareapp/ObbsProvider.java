package cm.aptoide.pt.spotandshareapp;

import java.io.File;

/**
 * Created by filipe on 13-07-2017.
 */

public class ObbsProvider {

  public File getMainObbFile(String obbsFilePath) {
    File obbsFolder = new File(obbsFilePath);
    File[] filesList = obbsFolder.listFiles();

    for (File file : filesList) {
      String fileName = file.getName();
      if (fileName.endsWith("obb")) {
        String[] fileNameArray = fileName.split("\\.");
        String prefix = fileNameArray[0];
        if (prefix.equalsIgnoreCase("main")) {
          return file;
        }
      }
    }
    return null;
  }

  public File getPatchObbFile(String obbsFilePath) {
    File obbsFolder = new File(obbsFilePath);
    File[] filesList = obbsFolder.listFiles();

    for (File file : filesList) {
      String fileName = file.getName();
      if (fileName.endsWith("obb")) {
        String[] fileNameArray = fileName.split("\\.");
        String prefix = fileNameArray[0];
        if (prefix.equalsIgnoreCase("patch")) {
          return file;
        }
      }
    }
    return null;
  }

  public File[] getObbsList(String obbsFilePath) {
    File[] obbs = new File[2];

    File obbsFolder = new File(obbsFilePath);
    File[] filesList = obbsFolder.listFiles();

    for (File file : filesList) {
      String fileName = file.getName();
      if (fileName.endsWith("obb")) {
        String[] fileNameArray = fileName.split("\\.");
        String prefix = fileNameArray[0];
        if (prefix.equalsIgnoreCase("main")) {
          obbs[0] = file;
        } else if (prefix.equalsIgnoreCase("patch")) {
          obbs[1] = file;
        }
      }
    }

    return obbs;
  }
}
