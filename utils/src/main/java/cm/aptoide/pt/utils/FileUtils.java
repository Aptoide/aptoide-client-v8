/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.logger.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/18/16.
 */
public class FileUtils {
  public static final String MOVE = "Move";
  public static final String COPY = "Copy";
  private static final String TAG = FileUtils.class.getSimpleName();
  private Action1<String> sendFileMoveEvent;

  /**
   * used to send an analytics event showing who the file was moved
   *
   * @param sendFileMoveEvent action to send the event
   */
  public FileUtils(@Nullable Action1<String> sendFileMoveEvent) {
    this.sendFileMoveEvent = sendFileMoveEvent;
  }

  public FileUtils() {
  }

  public static boolean removeFile(String filePAth) {
    boolean toReturn = false;
    if (!TextUtils.isEmpty(filePAth)) {
      toReturn = new File(filePAth).delete();
    }
    return toReturn;
  }

  public static void createDir(String path) {
    File dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }

  public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
      Bitmap.CompressFormat format, int quality) {

    File imageFile = new File(dir, fileName);

    FileOutputStream fos = null;
    try {
      dir.mkdirs();
      fos = new FileOutputStream(imageFile);

      bm.compress(format, quality, fos);

      fos.close();

      return true;
    } catch (IOException e) {
      Logger.e(TAG, e.getMessage());
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e1) {
          Logger.e(TAG, e1.getMessage());
        }
      }
    }
    return false;
  }

  public static boolean fileExists(String path) {
    return !TextUtils.isEmpty(path) && new File(path).exists();
  }

  public long deleteDir(File dir) {
    if (dir == null) {
      throw new RuntimeException("The file to be deleted can't be null");
    }
    long size = 0;
    if (dir.isDirectory()) {
      File[] children = dir.listFiles() == null ? new File[0] : dir.listFiles();
      for (File child : children) {
        size += deleteDir(child);
      }
    }
    size += dir.length();
    if (!dir.exists() || dir.delete()) {
      return size;
    } else {
      throw new RuntimeException("Something went wrong while deleting the file "
          + dir.getPath()
          + " (if the is the file a directory, is it empty?");
    }
  }

  /**
   * Return the size of a directory in bytes
   */
  public long dirSize(File dir) {

    long result = 0;
    if (dir.exists()) {
      File[] fileList = dir.listFiles();
      if (fileList != null) {
        for (int i = 0; i < fileList.length; i++) {
          // Recursive call if it's a directory
          if (fileList[i].isDirectory()) {
            result += dirSize(fileList[i]);
          } else {
            // Sum the file size in bytes
            result += fileList[i].length();
          }
        }
      }
    }
    return result;
  }

  /**
   * Method used to copy files from <code>inputPath</code> to <code>outputPath</code> <p>If any
   * exception occurs,
   * both
   * input and output files will be deleted</p>
   *
   * @param inputPath Path to the directory where the file to be copied is
   * @param outputPath Path to the directory where the file should be copied
   * @param fileName Name of the file to be copied
   */
  public void copyFile(String inputPath, String outputPath, String fileName) {
    if (!fileExists(inputPath + fileName)) {
      throw new RuntimeException("Input file(" + inputPath + fileName + ") doesn't exists");
    }

    File file = new File(inputPath + fileName);
    if (!file.renameTo(new File(outputPath + fileName))) {
      cloneFile(inputPath, outputPath, fileName);
    } else if (sendFileMoveEvent != null) {
      sendFileMoveEvent.call(MOVE);
    }
  }

  /**
   * this method clones a file, it opens the file and using a stream, the new file will be written
   *
   * @param inputPath Path to the directory where the file to be copied is
   * @param outputPath Path to the directory where the file should be copied
   * @param fileName Name of the file to be copied
   */
  public void cloneFile(String inputPath, String outputPath, String fileName) {
    InputStream in = null;
    OutputStream out = null;
    try {

      //create output directory if it doesn't exist
      File dir = new File(outputPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      in = new FileInputStream(inputPath + "/" + fileName);
      out = new FileOutputStream(outputPath + "/" + fileName);

      byte[] buffer = new byte[1024];
      int read;
      while ((read = in.read(buffer)) != -1) {
        out.write(buffer, 0, read);
      }
      in.close();

      // write the output file (You have now copied the file)
      out.flush();
      out.close();
      new File(inputPath + fileName).delete();
      if (sendFileMoveEvent != null) {
        sendFileMoveEvent.call(COPY);
      }
    } catch (Exception e) {
      File inputFile = new File(inputPath + "/" + fileName);
      if (inputFile.exists()) {
        inputFile.delete();
      }
      File outputFile = new File(outputPath + "/" + fileName);
      if (outputFile.exists()) {
        outputFile.delete();
      }
      Logger.e(TAG, e.getMessage());
      //				toReturn = false;
      throw new RuntimeException(e);
    } finally {
      in = null;
      out = null;
    }
  }

  public Observable<Long> deleteFolder(File... folders) {
    return Observable.from(folders)
        .observeOn(Schedulers.io())
        .flatMap(filePath -> Observable.fromCallable(() -> {
          long size = deleteDir(filePath);
          Logger.d(TAG, "deleting folder " + filePath.getPath() + " size: " + size);
          return size;
        })
            .onErrorResumeNext(throwable -> Observable.empty()))
        .toList()
        .map(deletedSizes -> {
          long size = 0;
          for (int i = 0; i < deletedSizes.size(); i++) {
            size += deletedSizes.get(i);
          }
          return size;
        });
  }

  public Observable<Long> deleteFolder(String... folders) {
    File[] files = new File[folders.length];
    for (int i = 0; i < folders.length; i++) {
      files[i] = new File(folders[i]);
    }
    return deleteFolder(files);
  }

  public String getPath(Uri contentUri, Context context) {
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      Cursor cursor = context.getContentResolver()
          .query(contentUri, proj, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } catch (Exception e) {
      return contentUri.getPath();
    }
  }
}
