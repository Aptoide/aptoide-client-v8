/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.store;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
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

  DEFAULT(R.style.AptoideThemeDefault, R.color.default_orange_gradient_start,
      R.color.default_orange_gradient_end, R.drawable.button_border_orange,
      R.drawable.create_store_theme_shape_default, R.drawable.main_orange_gradient,
      R.color.default_text, R.drawable.default_search_button_background, 8),

  GREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.green_gradient, R.color.green_text, R.drawable.default_search_button_background,
      8),

  TEAL(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.default_search_button_background,
      8),

  RED(R.style.AptoideThemeDefaultRed, R.color.red, R.color.red_gradient_end,
      R.drawable.button_border_red, R.drawable.create_store_theme_shape_red,
      R.drawable.red_gradient, R.color.red_text, R.drawable.default_search_button_background, 8),

  INDIGO(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.indigo_text,
      R.drawable.default_search_button_background, 8),

  PINK(R.style.AptoideThemeDefaultPink, R.color.pink, R.color.pink_gradient_end,
      R.drawable.button_border_pink, R.drawable.create_store_theme_shape_pink,
      R.drawable.pink_gradient, R.color.pink_text, R.drawable.default_search_button_background,
      8),

  ORANGE(R.style.AptoideThemeDefaultOrange, R.color.orange, R.color.orange_gradient_end,
      R.drawable.button_border_orange, R.drawable.create_store_theme_shape_orange,
      R.drawable.orange_gradient, R.color.orange_text,
      R.drawable.default_search_button_background, 8),

  BROWN(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.default_search_button_background,
      8),

  BLUE_GREY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_gradient_end,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey,
      R.drawable.blue_grey_gradient, R.color.blue_grey_text,
      R.drawable.default_search_button_background, 8),

  GREY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.default_search_button_background,
      8),

  BLACK(R.style.AptoideThemeDefaultBlack, R.color.black, R.color.grey,
      R.drawable.button_border_black, R.drawable.create_store_theme_shape_black,
      R.drawable.black_gradient, R.color.black_text, R.drawable.default_search_button_background,
      8),

  DEEP_PURPLE(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.default_search_button_background, 8),

  AMBER(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.default_search_button_background,
      8),

  LIGHT_GREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green,
      R.color.light_green_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.default_search_button_background, 8),

  LIME(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.default_search_button_background,
      8),

  LIGHT_BLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.default_search_button_background, 8),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN(R.style.AptoideThemeDefaultGreen, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.light_green_gradient, R.color.light_green_text,
      R.drawable.default_search_button_background, 7),

  //SLATEGRAY TO TEAL
  SLATE_GRAY(R.style.AptoideThemeDefaultTeal, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.default_search_button_background,
      7),

  //BLUE TO INDIGO
  BLUE(R.style.AptoideThemeDefaultIndigo, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.blue_grey_text,
      R.drawable.default_search_button_background, 7),

  //MAROON TO BROWN
  MAROON(R.style.AptoideThemeDefaultBrown, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.default_search_button_background,
      7),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_gradient_end,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey,
      R.drawable.blue_grey_gradient, R.color.blue_grey_text,
      R.drawable.default_search_button_background, 7),

  BLUE_GRAY(R.style.AptoideThemeDefaultBluegrey, R.color.blue_grey, R.color.blue_grey_gradient_end,
      R.drawable.button_border_bluegrey, R.drawable.create_store_theme_shape_blue_grey,
      R.drawable.blue_grey_gradient, R.color.blue_grey_text,
      R.drawable.default_search_button_background, 7),

  //SILVER AND DIMGREY TO GREY
  SILVER(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.default_search_button_background,
      7),

  DIM_GRAY(R.style.AptoideThemeDefaultGrey, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.default_search_button_background,
      7),

  //MAGENTA TO DEEPPURPLE
  MAGENTA(R.style.AptoideThemeDefaultDeepPurple, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.default_search_button_background, 7),

  //YELLOW AND GOLD TO AMBER
  YELLOW(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.default_search_button_background,
      7),

  GOLD(R.style.AptoideThemeDefaultAmber, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.default_search_button_background,
      7),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN(R.style.AptoideThemeDefaultLightgreen, R.color.light_green,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.default_search_button_background, 7),

  //GREENAPPLE TO LIME
  GREEN_APPLE(R.style.AptoideThemeDefaultLime, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.default_search_button_background,
      7),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.default_search_button_background, 7),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE(R.style.AptoideThemeDefaultLightblue, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.default_search_button_background, 7);

  @ColorRes private final int darkerColor;
  @DrawableRes private final int buttonDrawable;
  @DrawableRes private final int roundDrawable;
  @ColorRes private final int colorLetters;
  @ColorRes private int storeStyle;
  @ColorRes private int primaryColor;
  @DrawableRes private int gradientDrawable;
  @DrawableRes private int roundGradientButtonDrawable;
  private int version;

  StoreTheme(@StyleRes int storeStyle, @ColorRes int primaryColor, @ColorRes int darkerColor,
      @DrawableRes int buttonDrawable, @DrawableRes int roundDrawable,
      @DrawableRes int gradientDrawable, int colorLetters, int roundGradientButtonDrawable,
      int version) {
    this.storeStyle = storeStyle;
    this.primaryColor = primaryColor;
    this.darkerColor = darkerColor;
    this.buttonDrawable = buttonDrawable;
    this.roundDrawable = roundDrawable;
    this.gradientDrawable = gradientDrawable;
    this.colorLetters = colorLetters;
    this.roundGradientButtonDrawable = roundGradientButtonDrawable;
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
      if (storeThemeName != null) {
        theme = valueOf(storeThemeName.replace("-", "_")
            .toUpperCase());
      }
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

  @DrawableRes public int getGradientDrawable() {
    return gradientDrawable;
  }

  @ColorRes public int getColorLetters() {
    return colorLetters;
  }

  @DrawableRes public int getRoundGradientButtonDrawable() {
    return roundGradientButtonDrawable;
  }
}
