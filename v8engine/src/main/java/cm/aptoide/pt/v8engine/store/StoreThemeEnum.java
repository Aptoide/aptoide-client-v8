/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.store;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 05-05-2016.
 */
public enum StoreThemeEnum {

  APTOIDE_STORE_THEME_DEFAULT(R.style.AptoideThemeDefault, R.color.default_color,
      R.color.default_color_700, R.drawable.button_border_orange),

  APTOIDE_STORE_THEME_GREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_700,
      R.drawable.button_border_green),

  APTOIDE_STORE_THEME_TEAL(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_700,
      R.drawable.button_border_teal),

  APTOIDE_STORE_THEME_RED(R.style.AptoideThemeDefaultRed, R.color.red, R.color.red_700,
      R.drawable.button_border_red),

  APTOIDE_STORE_THEME_INDIGO(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_700,
      R.drawable.button_border_indigo),

  APTOIDE_STORE_THEME_PINK(R.style.AptoideThemeDefaultPink, R.color.pink, R.color.pink_700,
      R.drawable.button_border_pink),

  APTOIDE_STORE_THEME_ORANGE(R.style.AptoideThemeDefaultOrange, R.color.orange, R.color.orange_700,
      R.drawable.button_border_orange),

  APTOIDE_STORE_THEME_BROWN(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_700,
      R.drawable.button_border_brown),

  //Alternative name to BLUEGREY
  APTOIDE_STORE_THEME_BLUEGRAY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey,
      R.color.blue_grey_700, R.drawable.button_border_bluegrey),

  APTOIDE_STORE_THEME_BLUEGREY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey,
      R.color.blue_grey_700, R.drawable.button_border_bluegrey),

  APTOIDE_STORE_THEME_GREY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey),

  APTOIDE_STORE_THEME_BLACK(R.style.AptoideThemeDefaultBlack, R.color.black, R.color.grey,
      R.drawable.button_border_black),

  APTOIDE_STORE_THEME_DEEPPURPLE(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple,
      R.color.deep_purple_700, R.drawable.button_border_deeppurple),

  APTOIDE_STORE_THEME_AMBER(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber),

  APTOIDE_STORE_THEME_LIGHTGREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green,
      R.color.light_green_700, R.drawable.button_border_lightgreen),

  APTOIDE_STORE_THEME_LIME(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_700,
      R.drawable.button_border_lime),

  APTOIDE_STORE_THEME_LIGHTBLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_700, R.drawable.button_border_lightblue),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  APTOIDE_STORE_THEME_SEAGREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_700,
      R.drawable.button_border_green),

  //SLATEGRAY TO TEAL
  APTOIDE_STORE_THEME_SLATEGRAY(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_700,
      R.drawable.button_border_teal),

  //BLUE TO INDIGO
  APTOIDE_STORE_THEME_BLUE(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_700,
      R.drawable.button_border_indigo),

  //MAROON TO BROWN
  APTOIDE_STORE_THEME_MAROON(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_700,
      R.drawable.button_border_brown),

  //MIDNIGHT TO BLUEGREY
  APTOIDE_STORE_THEME_MIDNIGHT(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey,
      R.color.blue_grey_700, R.drawable.button_border_bluegrey),

  //SILVER AND DIMGREY TO GREY
  APTOIDE_STORE_THEME_SILVER(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey),

  APTOIDE_STORE_THEME_DIMGRAY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey),

  //MAGENTA TO DEEPPURPLE
  APTOIDE_STORE_THEME_MAGENTA(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple,
      R.color.deep_purple_700, R.drawable.button_border_deeppurple),

  //YELLOW AND GOLD TO AMBER
  APTOIDE_STORE_THEME_YELLOW(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber),

  APTOIDE_STORE_THEME_GOLD(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber),

  //SPRINGGREEN TO LIGHTGREEN
  APTOIDE_STORE_THEME_SPRINGGREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green,
      R.color.light_green_700, R.drawable.button_border_lightgreen),

  //GREENAPPLE TO LIME
  APTOIDE_STORE_THEME_GREENAPPLE(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_700,
      R.drawable.button_border_lime),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  APTOIDE_STORE_THEME_LIGHTSKY(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_700, R.drawable.button_border_lightblue),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  APTOIDE_STORE_THEME_HAPPYBLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_700, R.drawable.button_border_lightblue),;

  private final int color700tint;
  private final int buttonLayout;
  private int storeHeader;
  private int storeThemeResource;

  StoreThemeEnum(int storeThemeResource, int storeHeader, int color700tint, int buttonLayout) {

    this.storeThemeResource = storeThemeResource;
    this.storeHeader = storeHeader;
    this.color700tint = color700tint;
    this.buttonLayout = buttonLayout;
  }

  public static StoreThemeEnum reverseOrdinal(int ordinal) {
    return values()[ordinal];
  }

  public static StoreThemeEnum get(Store store) {
    return get(store.getAppearance()
        .getTheme());
  }

  public static StoreThemeEnum get(String s) {

    StoreThemeEnum theme;
    try {
      s = s.replace("-", "");
      theme = valueOf("APTOIDE_STORE_THEME_" + s.toUpperCase());
    } catch (Exception e) {
      theme = APTOIDE_STORE_THEME_DEFAULT;
    }

    return theme;
  }

  public static StoreThemeEnum get(int i) {

    StoreThemeEnum theme;
    try {
      theme = values()[i];
    } catch (Exception e) {
      theme = APTOIDE_STORE_THEME_DEFAULT;
    }

    return theme;
  }

  @ColorInt public int getStoreHeaderInt(Context context) {
    return context.getResources()
        .getColor(getStoreHeader());
  }

  /**
   * Used on AppBar
   */
  @ColorRes public int getStoreHeader() {
    return storeHeader;
  }

  public int getThemeResource() {
    return storeThemeResource;
  }

  /**
   * Used on StatusBar (Lollipop and higher)
   */
  @ColorRes public int getColor700tint() {
    return color700tint;
  }

  public Drawable getButtonLayoutDrawable(Context context) {
    return context.getResources()
        .getDrawable(getButtonLayout());
  }

  @DrawableRes public int getButtonLayout() {
    return buttonLayout;
  }
}
