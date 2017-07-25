/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.store;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 05-05-2016.
 */
/*
 * › Allowed values: "default", "amber", "black", "blue-grey", "brown", "deep-purple", "green",
 * "grey", "indigo", "light-blue", "light-green", "lime", "orange", "pink", "red", "teal"
 *
 * from http://ws75-primary.aptoide.com/api/7/store/set/info/1
 *
 */
public enum StoreTheme {

  DEFAULT(R.style.AptoideThemeDefault, R.color.default_color, R.color.default_color_700,
      R.drawable.button_border_orange, R.drawable.create_store_theme_shape_default, 8),

  GREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_700,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green, 8),

  TEAL(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_700,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal, 8),

  RED(R.style.AptoideThemeDefaultRed, R.color.red, R.color.red_700, R.drawable.button_border_red,
      R.drawable.create_store_theme_shape_red, 8),

  INDIGO(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_700,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo, 8),

  PINK(R.style.AptoideThemeDefaultPink, R.color.pink, R.color.pink_700,
      R.drawable.button_border_pink, R.drawable.create_store_theme_shape_pink, 8),

  ORANGE(R.style.AptoideThemeDefaultOrange, R.color.orange, R.color.orange_700,
      R.drawable.button_border_orange, R.drawable.create_store_theme_shape_orange, 8),

  BROWN(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_700,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown, 8),

  BLUE_GREY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_700,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey, 8),

  GREY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey, 8),

  BLACK(R.style.AptoideThemeDefaultBlack, R.color.black, R.color.grey,
      R.drawable.button_border_black, R.drawable.create_store_theme_shape_black, 8),

  DEEP_PURPLE(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple, R.color.deep_purple_700,
      R.drawable.button_border_deeppurple, R.drawable.create_store_theme_shape_deep_purple, 8),

  AMBER(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber, 8),

  LIGHT_GREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green, R.color.light_green_700,
      R.drawable.button_border_lightgreen, R.drawable.create_store_theme_shape_light_green, 8),

  LIME(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_700,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime, 8),

  LIGHT_BLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue, R.color.light_blue_700,
      R.drawable.button_border_lightblue, R.drawable.create_store_theme_shape_lightblue, 8),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_700,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green, 7),

  //SLATEGRAY TO TEAL
  SLATE_GRAY(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_700,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal, 7),

  //BLUE TO INDIGO
  BLUE(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_700,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo, 7),

  //MAROON TO BROWN
  MAROON(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_700,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown, 7),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_700,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey, 7),

  BLUE_GRAY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_700,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey, 7),

  //SILVER AND DIMGREY TO GREY
  SILVER(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey, 7),

  DIM_GRAY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_700,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey, 7),

  //MAGENTA TO DEEPPURPLE
  MAGENTA(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple, R.color.deep_purple_700,
      R.drawable.button_border_deeppurple, R.drawable.create_store_theme_shape_deep_purple, 7),

  //YELLOW AND GOLD TO AMBER
  YELLOW(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber, 7),

  GOLD(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_700,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber, 7),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green, R.color.light_green_700,
      R.drawable.button_border_lightgreen, R.drawable.create_store_theme_shape_light_green, 7),

  //GREENAPPLE TO LIME
  GREEN_APPLE(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_700,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime, 7),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY(R.style.AptoideThemeDefaultLightblue, R.color.light_blue, R.color.light_blue_700,
      R.drawable.button_border_lightblue, R.drawable.create_store_theme_shape_lightblue, 7),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue, R.color.light_blue_700,
      R.drawable.button_border_lightblue, R.drawable.create_store_theme_shape_lightblue, 7);

  @ColorRes private final int darkerColor;
  @DrawableRes private final int buttonDrawable;
  @DrawableRes private final int roundDrawable;
  @ColorRes private int storeStyle;
  @ColorRes private int primaryColor;
  private int version;

  StoreTheme(@StyleRes int storeStyle, @ColorRes int primaryColor, @ColorRes int darkerColor,
      @DrawableRes int buttonDrawable, @DrawableRes int roundDrawable, int version) {
    this.storeStyle = storeStyle;
    this.primaryColor = primaryColor;
    this.darkerColor = darkerColor;
    this.buttonDrawable = buttonDrawable;
    this.roundDrawable = roundDrawable;
    this.version = version;
  }

  public static List<StoreTheme> getThemesFromVersion(int version) {
    List<StoreTheme> themes = new LinkedList<>();
    for (StoreTheme theme : values()) {
      if (theme.getVersion() == version) {
        themes.add(theme);
      }
    }
    return themes;
  }

  public static StoreTheme get(Store store) {
    return get(store.getAppearance()
        .getTheme());
  }

  public static StoreTheme get(String storeThemeName) {
    StoreTheme theme = null;
    try {
      theme = valueOf(storeThemeName.replace("-", "_")
          .toUpperCase());
    } catch (IllegalArgumentException e) {
    }

    if (theme == null) {
      theme = DEFAULT;
    }

    return theme;
  }

  public static StoreTheme get(int i) {
    StoreTheme theme = null;
    try {
      theme = values()[i];
    } catch (ArrayIndexOutOfBoundsException e) {
    }

    if (theme == null) {
      theme = DEFAULT;
    }

    return theme;
  }

  public static StoreTheme fromName(String themeName) {
    String storeThemeName = themeName.toLowerCase();
    for (StoreTheme theme : StoreTheme.values()) {
      if (TextUtils.equals(theme.getThemeName(), storeThemeName)) {
        return theme;
      }
    }
    // default case
    return StoreTheme.DEFAULT;
  }

  public int getVersion() {
    return version;
  }

  @DrawableRes public int getRoundDrawable() {
    return roundDrawable;
  }

  @ColorInt public int getStoreHeaderColorResource(Resources resources, Resources.Theme theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return resources.getColor(getPrimaryColor(), theme);
    } else {
      return resources.getColor(getPrimaryColor());
    }
  }

  /**
   * Used on AppBar
   */
  @ColorRes public int getPrimaryColor() {
    return primaryColor;
  }

  @StyleRes public int getThemeResource() {
    return storeStyle;
  }

  /**
   * Used on StatusBar (Lollipop and higher)
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @ColorRes public int getDarkerColor() {
    return darkerColor;
  }

  public Drawable getButtonLayoutDrawable(Resources resources, Resources.Theme theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return resources.getDrawable(getButtonDrawable(), theme);
    } else {
      return resources.getDrawable(getButtonDrawable());
    }
  }

  @DrawableRes public int getButtonDrawable() {
    return buttonDrawable;
  }

  public String getThemeName() {
    return name().toLowerCase()
        .replace('_', '-');
  }
}
