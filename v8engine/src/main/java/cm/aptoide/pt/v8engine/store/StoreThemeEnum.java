/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.store;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 05-05-2016.
 */
public enum StoreThemeEnum {
  //TODO: Limpar os enums de tudo o que não é necessário ter (drawables, etc)
  APTOIDE_STORE_THEME_DEFAULT(R.style.AptoideThemeDefault, R.color.transparent_orange,
      R.color.default_color, R.drawable.custom_categ_orange, R.drawable.gradient_black,
      R.color.default_color_700, R.drawable.button_border_orange, R.drawable.ic_check_orange,
      R.drawable.ic_plus_orange),

  APTOIDE_STORE_THEME_GREEN(R.style.AptoideThemeDefaultGreen, R.color.transparent_green,
      R.color.green, R.drawable.custom_categ_green, R.drawable.gradient_green, R.color.green_700,
      R.drawable.button_border_green, R.drawable.ic_check_green, R.drawable.ic_plus_green),

  APTOIDE_STORE_THEME_TEAL(R.style.AptoideThemeDefaultTeal, R.color.transparent_teal, R.color.teal,
      R.drawable.custom_categ_slategray, R.drawable.gradient_slategray, R.color.teal_700,
      R.drawable.button_border_teal, R.drawable.ic_check_teal, R.drawable.ic_plus_teal),

  APTOIDE_STORE_THEME_RED(R.style.AptoideThemeDefaultRed, R.color.transparent_red, R.color.red,
      R.drawable.custom_categ_red, R.drawable.gradient_red, R.color.red_700,
      R.drawable.button_border_red, R.drawable.ic_check_red, R.drawable.ic_check_red),

  APTOIDE_STORE_THEME_INDIGO(R.style.AptoideThemeDefaultIndigo, R.color.transparent_indigo,
      R.color.indigo, R.drawable.custom_categ_blue, R.drawable.gradient_blue, R.color.indigo_700,
      R.drawable.button_border_indigo, R.drawable.ic_check_indigo, R.drawable.ic_plus_indigo),

  APTOIDE_STORE_THEME_PINK(R.style.AptoideThemeDefaultPink, R.color.transparent_pink, R.color.pink,
      R.drawable.custom_categ_pink, R.drawable.gradient_pink, R.color.pink_700,
      R.drawable.button_border_pink, R.drawable.ic_check_pink, R.drawable.ic_plus_pink),

  APTOIDE_STORE_THEME_ORANGE(R.style.AptoideThemeDefaultOrange, R.color.transparent_orange,
      R.color.orange, R.drawable.custom_categ_orange, R.drawable.gradient_orange,
      R.color.orange_700, R.drawable.button_border_orange, R.drawable.ic_check_orange,
      R.drawable.ic_plus_orange),

  APTOIDE_STORE_THEME_BROWN(R.style.AptoideThemeDefaultBrown, R.color.transparent_brown,
      R.color.brown, R.drawable.custom_categ_maroon, R.drawable.gradient_maroon, R.color.brown_700,
      R.drawable.button_border_brown, R.drawable.ic_check_brown, R.drawable.ic_plus_brown),

  //Alternative name to BLUEGREY
  APTOIDE_STORE_THEME_BLUEGRAY(R.style.AptoideThemeDefaultBluegrey, R.color.transparent_bluegrey,
      R.color.bluegrey, R.drawable.custom_categ_midnight, R.drawable.gradient_midnight,
      R.color.bluegrey_700, R.drawable.button_border_bluegrey, R.drawable.ic_check_blue_grey,
      R.drawable.ic_plus_blue_grey),

  APTOIDE_STORE_THEME_BLUEGREY(R.style.AptoideThemeDefaultBluegrey, R.color.transparent_bluegrey,
      R.color.bluegrey, R.drawable.custom_categ_midnight, R.drawable.gradient_midnight,
      R.color.bluegrey_700, R.drawable.button_border_bluegrey, R.drawable.ic_check_blue_grey,
      R.drawable.ic_plus_blue_grey),

  APTOIDE_STORE_THEME_GREY(R.style.AptoideThemeDefaultGrey, R.color.transparent_grey, R.color.grey,
      R.drawable.custom_categ_silver, R.drawable.gradient_silver, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.ic_check_grey, R.drawable.ic_check_grey),

  APTOIDE_STORE_THEME_BLACK(R.style.AptoideThemeDefaultBlack, R.color.transparent_black,
      R.color.black, R.drawable.custom_categ_black, R.drawable.gradient_black, R.color.grey,
      R.drawable.button_border_black, R.drawable.ic_check_black, R.drawable.ic_plus_black),

  APTOIDE_STORE_THEME_DEEPPURPLE(R.style.AptoideThemeDefaultDeepPurple,
      R.color.transparent_deeppurple, R.color.deeppurple, R.drawable.custom_categ_magenta,
      R.drawable.gradient_magenta, R.color.deeppurple_700, R.drawable.button_border_deeppurple,
      R.drawable.ic_check_deep_purple, R.drawable.ic_plus_deep_purple),

  APTOIDE_STORE_THEME_AMBER(R.style.AptoideThemeDefaultAmber, R.color.transparent_amber,
      R.color.amber, R.drawable.custom_categ_gold, R.drawable.gradient_gold, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.ic_check_amber, R.drawable.ic_plus_amber),

  APTOIDE_STORE_THEME_LIGHTGREEN(R.style.AptoideThemeDefaultLightgreen,
      R.color.transparent_lightgreen, R.color.lightgreen, R.drawable.custom_categ_springgreen,
      R.drawable.gradient_springgreen, R.color.lightgreen_700, R.drawable.button_border_lightgreen,
      R.drawable.ic_check_light_green, R.drawable.ic_plus_light_green),

  APTOIDE_STORE_THEME_LIME(R.style.AptoideThemeDefaultLime, R.color.transparent_lime, R.color.lime,
      R.drawable.custom_categ_springgreen, R.drawable.gradient_springgreen, R.color.lime_700,
      R.drawable.button_border_lime, R.drawable.ic_check_lime, R.drawable.ic_plus_lime),

  APTOIDE_STORE_THEME_LIGHTBLUE(R.style.AptoideThemeDefaultLightblue, R.color.transparent_lightblue,
      R.color.lightblue, R.drawable.custom_categ_lightsky, R.drawable.gradient_lightsky,
      R.color.lightblue_700, R.drawable.button_border_lightblue, R.drawable.ic_check_light_blue,
      R.drawable.ic_plus_light_blue),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  APTOIDE_STORE_THEME_SEAGREEN(R.style.AptoideThemeDefaultGreen, R.color.transparent_green,
      R.color.green, R.drawable.custom_categ_green, R.drawable.gradient_green, R.color.green_700,
      R.drawable.button_border_green, R.drawable.ic_check_green, R.drawable.ic_plus_green),

  //SLATEGRAY TO TEAL
  APTOIDE_STORE_THEME_SLATEGRAY(R.style.AptoideThemeDefaultTeal, R.color.transparent_teal,
      R.color.teal, R.drawable.custom_categ_slategray, R.drawable.gradient_slategray,
      R.color.teal_700, R.drawable.button_border_teal, R.drawable.ic_check_teal,
      R.drawable.ic_plus_teal),

  //BLUE TO INDIGO
  APTOIDE_STORE_THEME_BLUE(R.style.AptoideThemeDefaultIndigo, R.color.transparent_indigo,
      R.color.indigo, R.drawable.custom_categ_blue, R.drawable.gradient_blue, R.color.indigo_700,
      R.drawable.button_border_indigo, R.drawable.ic_check_indigo, R.drawable.ic_plus_indigo),

  //MAROON TO BROWN
  APTOIDE_STORE_THEME_MAROON(R.style.AptoideThemeDefaultBrown, R.color.transparent_brown,
      R.color.brown, R.drawable.custom_categ_maroon, R.drawable.gradient_maroon, R.color.brown_700,
      R.drawable.button_border_brown, R.drawable.ic_check_brown, R.drawable.ic_plus_brown),

  //MIDNIGHT TO BLUEGREY
  APTOIDE_STORE_THEME_MIDNIGHT(R.style.AptoideThemeDefaultBluegrey, R.color.transparent_bluegrey,
      R.color.bluegrey, R.drawable.custom_categ_midnight, R.drawable.gradient_midnight,
      R.color.bluegrey_700, R.drawable.button_border_bluegrey, R.drawable.ic_check_blue_grey,
      R.drawable.ic_plus_blue_grey),

  //SILVER AND DIMGREY TO GREY
  APTOIDE_STORE_THEME_SILVER(R.style.AptoideThemeDefaultGrey, R.color.transparent_grey,
      R.color.grey, R.drawable.custom_categ_silver, R.drawable.gradient_silver, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.ic_check_grey, R.drawable.ic_plus_grey),

  APTOIDE_STORE_THEME_DIMGRAY(R.style.AptoideThemeDefaultGrey, R.color.transparent_grey,
      R.color.grey, R.drawable.custom_categ_silver, R.drawable.gradient_silver, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.ic_check_grey, R.drawable.ic_plus_grey),

  //MAGENTA TO DEEPPURPLE
  APTOIDE_STORE_THEME_MAGENTA(R.style.AptoideThemeDefaultDeepPurple, R.color.transparent_deeppurple,
      R.color.deeppurple, R.drawable.custom_categ_magenta, R.drawable.gradient_magenta,
      R.color.deeppurple_700, R.drawable.button_border_deeppurple, R.drawable.ic_check_deep_purple,
      R.drawable.ic_plus_deep_purple),

  //YELLOW AND GOLD TO AMBER
  APTOIDE_STORE_THEME_YELLOW(R.style.AptoideThemeDefaultAmber, R.color.transparent_amber,
      R.color.amber, R.drawable.custom_categ_gold, R.drawable.gradient_gold, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.ic_check_amber, R.drawable.ic_plus_amber),

  APTOIDE_STORE_THEME_GOLD(R.style.AptoideThemeDefaultAmber, R.color.transparent_amber,
      R.color.amber, R.drawable.custom_categ_gold, R.drawable.gradient_gold, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.ic_check_amber, R.drawable.ic_plus_amber),

  //SPRINGGREEN TO LIGHTGREEN
  APTOIDE_STORE_THEME_SPRINGGREEN(R.style.AptoideThemeDefaultLightgreen,
      R.color.transparent_lightgreen, R.color.lightgreen, R.drawable.custom_categ_springgreen,
      R.drawable.gradient_springgreen, R.color.lightgreen_700, R.drawable.button_border_lightgreen,
      R.drawable.ic_check_light_green, R.drawable.ic_plus_light_green),

  //GREENAPPLE TO LIME
  APTOIDE_STORE_THEME_GREENAPPLE(R.style.AptoideThemeDefaultLime, R.color.transparent_lime,
      R.color.lime, R.drawable.custom_categ_springgreen, R.drawable.gradient_springgreen,
      R.color.lime_700, R.drawable.button_border_lime, R.drawable.ic_check_lime,
      R.drawable.ic_plus_lime),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  APTOIDE_STORE_THEME_LIGHTSKY(R.style.AptoideThemeDefaultLightblue, R.color.transparent_lightblue,
      R.color.lightblue, R.drawable.custom_categ_lightsky, R.drawable.gradient_lightsky,
      R.color.lightblue_700, R.drawable.button_border_lightblue, R.drawable.ic_check_light_blue,
      R.drawable.ic_plus_light_blue),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  APTOIDE_STORE_THEME_HAPPYBLUE(R.style.AptoideThemeDefaultLightblue, R.color.transparent_lightblue,
      R.color.lightblue, R.drawable.custom_categ_lightsky, R.drawable.gradient_lightsky,
      R.color.lightblue_700, R.drawable.button_border_lightblue, R.drawable.ic_check_light_blue,
      R.drawable.ic_plus_light_blue),;

  private final int color700tint;
  private final int buttonLayout;
  private final int checkmarkDrawable;
  private final int plusmarkDrawable;
  private int storeHeader;
  private int storeCategoryDrawable;
  private int storeViewGradient;
  private int storeAlphaColor;
  private int storeThemeResource;

  StoreThemeEnum(int storeThemeResource, int storeAlphaColor, int storeHeader,
      int storeCategoryDrawable, int storeViewGradient, int color700tint, int buttonLayout,
      int checkmarkDrawable, int plusmarkDrawable) {

    this.storeThemeResource = storeThemeResource;
    this.storeAlphaColor = storeAlphaColor;
    this.storeHeader = storeHeader;
    this.storeCategoryDrawable = storeCategoryDrawable;
    this.storeViewGradient = storeViewGradient;
    this.color700tint = color700tint;
    this.buttonLayout = buttonLayout;
    this.checkmarkDrawable = checkmarkDrawable;
    this.plusmarkDrawable = plusmarkDrawable;
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

  public int getPlusmarkDrawable() {
    return plusmarkDrawable;
  }

  @ColorInt public int getStoreHeaderInt() {
    return V8Engine.getContext()
        .getResources()
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

  @ColorRes public int getStoreAlphaColor() {
    return storeAlphaColor;
  }

  @DrawableRes public int getStoreCategoryDrawable() {
    return storeCategoryDrawable;
  }

  @DrawableRes public int getStoreViewGradient() {
    return storeViewGradient;
  }

  /**
   * Used on StatusBar (Lollipop and higher)
   */
  @ColorRes public int getColor700tint() {
    return color700tint;
  }

  public Drawable getButtonLayoutDrawable() {
    return V8Engine.getContext()
        .getResources()
        .getDrawable(getButtonLayout());
  }

  @DrawableRes public int getButtonLayout() {
    return buttonLayout;
  }

  @DrawableRes public int getCheckmarkDrawable() {
    return checkmarkDrawable;
  }

}
