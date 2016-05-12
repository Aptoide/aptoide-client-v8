/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 03-12-2013
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class IconSizeUtils {

	static final private int baseLine = 96;
	static final private int baseLineAvatar = 150;
	static final private int baseLineXNotification = 320;
	static final private int baseLineYNotification = 180;
	private static int baseLineScreenshotLand = 256;
	private static int baseLineScreenshotPort = 96;


	public static String generateSizeStringNotification(Context context){

		if(context == null){
			return "";
		}

		int density = context.getResources().getDisplayMetrics().densityDpi;
		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		if (densityMultiplier <= 0.75f) {
			densityMultiplier = 0.75f;
		} else if (densityMultiplier <= 1) {
			densityMultiplier = 1f;
		} else if (densityMultiplier <= 1.333f) {
			densityMultiplier = 1.3312500f;
		} else if (densityMultiplier <= 1.5f) {
			densityMultiplier = 1.5f;
		} else if (densityMultiplier <= 2f) {
			densityMultiplier = 2f;
		}else if (densityMultiplier <= 3f) {
			densityMultiplier = 3f;
		} else {
			densityMultiplier = 4f;
		}



//        switch (density){
//            case 213:
//                densityMultiplier = 1.5f;
//                break;
//        }

		int sizeX = (int) (baseLineXNotification * densityMultiplier);
		int sizeY = (int) (baseLineYNotification * densityMultiplier);

		//Log.d("Aptoide-IconSize", "Size is " + size);

		return sizeX+"x"+sizeY;
	}

	public static String generateSizeString(Context context){

		if(context == null){
			return "";
		}

		int density = context.getResources().getDisplayMetrics().densityDpi;
		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		if (densityMultiplier <= 0.75f) {
			densityMultiplier = 0.75f;
		} else if (densityMultiplier <= 1) {
			densityMultiplier = 1f;
		} else if (densityMultiplier <= 1.333f) {
			densityMultiplier = 1.3312500f;
		} else if (densityMultiplier <= 1.5f) {
			densityMultiplier = 1.5f;
		} else if (densityMultiplier <= 2f) {
			densityMultiplier = 2f;
		}else if (densityMultiplier <= 3f) {
			densityMultiplier = 3f;
		} else {
			densityMultiplier = 4f;
		}




//        switch (density){
//            case 213:
//                densityMultiplier = 1.5f;
//                break;
//        }

		int size = (int) (baseLine * densityMultiplier);

		//Log.d("Aptoide-IconSize", "Size is " + size);

		return size+"x"+size;
	}


	public static String generateSizeStringAvatar(Context context) {

		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		if (densityMultiplier <= 0.75f) {
			densityMultiplier = 0.75f;
		} else if (densityMultiplier <= 1) {
			densityMultiplier = 1f;
		} else if (densityMultiplier <= 1.333f) {
			densityMultiplier = 1.3312500f;
		} else if (densityMultiplier <= 1.5f) {
			densityMultiplier = 1.5f;
		} else if (densityMultiplier <= 2f) {
			densityMultiplier = 2f;
		}else if (densityMultiplier <= 3f) {
			densityMultiplier = 3f;
		}else {
			densityMultiplier = 4f;
		}

//        switch (density){
//            case 213:
//                densityMultiplier = 1.5f;
//                break;
//        }

		int size = Math.round(baseLineAvatar * densityMultiplier);

		//Log.d("Aptoide-IconSize", "Size is " + size);

		return size+"x"+size;
	}

	public static String generateSizeStringScreenshots(Context context, String orient) {
		//int density = context.getResources().getDisplayMetrics().densityDpi;
		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		//Log.d("Aptoide-IconSize", "Original mult is" + densityMultiplier);

		if (densityMultiplier <= 0.75f) {
			densityMultiplier = 0.75f;
		} else if (densityMultiplier <= 1) {
			densityMultiplier = 1f;
		} else if (densityMultiplier <= 1.333f) {
			densityMultiplier = 1.3312500f;
		} else if (densityMultiplier <= 1.5f) {
			densityMultiplier = 1.5f;
		} else if (densityMultiplier <= 2f) {
			densityMultiplier = 2f;
		}else if (densityMultiplier <= 3f) {
			densityMultiplier = 3f;
		}else {
			densityMultiplier = 4f;
		}

		int size;
		if(orient != null && orient.equals("portrait")){
			size =  baseLineScreenshotPort * ((int) densityMultiplier);
		}else{
			size = baseLineScreenshotLand * ((int) densityMultiplier);
		}

		//Log.d("Aptoide-IconSize", "Size is " + size + " baseline is " + baseLineScreenshotPort + " with multiplier " +densityMultiplier );

		SystemUtils.context = context;
		return size + "x" + SystemUtils.getDensityDpi();
	}



}
