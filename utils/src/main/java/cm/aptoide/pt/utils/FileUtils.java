/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 5/18/16.
 */
public class FileUtils {

	private static final String TAG = FileUtils.class.getSimpleName();
	public static boolean fileExists(String path) {
		return !TextUtils.isEmpty(path) && new File(path).exists();
	}

	/**
	 * Method used to copy files from <code>inputPath</code> to <code>outputPath</code> <p>If any exception occurs,
	 * both
	 * input and output files will be deleted</p>
	 *
	 * @param inputPath  Path to the directory where the file to be copied is
	 * @param outputPath Path to the directory where the file should be copied
	 * @param fileName   Name of the file to be copied
	 *
	 * @return true if the the file was copied successfully, false otherwise
	 */
	public static void copyFile(String inputPath, String outputPath, String fileName) {
		File file = new File(inputPath + fileName);
		if (!file.renameTo(new File(outputPath + fileName))) {
			altCopyFile(inputPath, outputPath, fileName);
		}
	}

	public static void altCopyFile(String inputPath, String outputPath, String fileName) {
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

	/**
	 * Return the size of a directory in bytes
	 */
	public static long dirSize(File dir) {

		long result = 0;
		if (dir.exists()) {
			File[] fileList = dir.listFiles();
			for (int i = 0 ; i < fileList.length ; i++) {
				// Recursive call if it's a directory
				if (fileList[i].isDirectory()) {
					result += dirSize(fileList[i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
		}
		return result;
	}
}
