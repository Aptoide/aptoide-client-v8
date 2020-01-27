/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.themes;

import androidx.annotation.StyleRes;
import cm.aptoide.pt.R;
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

  DEFAULT(R.style.AptoideThemeLight, true), DEFAULT_DARK(R.style.AptoideThemeDark, false),

  GREEN(R.style.AptoideThemeLight_Green, true), GREEN_DARK(R.style.AptoideThemeDark_Green, false),

  TEAL(R.style.AptoideThemeLight_Teal, true), TEAL_DARK(R.style.AptoideThemeDark_Teal, false),

  RED(R.style.AptoideThemeLight_Red, true), RED_DARK(R.style.AptoideThemeDark_Red, false),

  INDIGO(R.style.AptoideThemeLight_Indigo, true), INDIGO_DARK(R.style.AptoideThemeDark_Indigo,
      false),

  PINK(R.style.AptoideThemeLight_Pink, true), PINK_DARK(R.style.AptoideThemeDark_Pink, false),

  ORANGE(R.style.AptoideThemeLight_Orange, true), ORANGE_DARK(R.style.AptoideThemeDark_Orange,
      false),

  BROWN(R.style.AptoideThemeLight_Brown, true), BROWN_DARK(R.style.AptoideThemeDark_Brown, false),

  BLUE_GREY(R.style.AptoideThemeLight_BlueGrey, true), BLUE_GREY_DARK(
      R.style.AptoideThemeDark_BlueGrey, false),

  GREY(R.style.AptoideThemeLight_Grey, true), GREY_DARK(R.style.AptoideThemeDark_Grey, false),

  BLACK(R.style.AptoideThemeLight_Black, true), BLACK_DARK(R.style.AptoideThemeDark_Black, false),

  DEEP_PURPLE(R.style.AptoideThemeLight_DeepPurple, true), DEEP_PURPLE_DARK(
      R.style.AptoideThemeDark_DeepPurple, false),

  AMBER(R.style.AptoideThemeLight_Amber, true), AMBER_DARK(R.style.AptoideThemeDark_Amber, false),

  LIGHT_GREEN(R.style.AptoideThemeLight_LightGreen, true), LIGHT_GREEN_DARK(
      R.style.AptoideThemeDark_LightGreen, false),

  LIME(R.style.AptoideThemeLight_Lime, true), LIME_DARK(R.style.AptoideThemeDark_Lime, false),

  LIGHT_BLUE(R.style.AptoideThemeLight_LightBlue, true), LIGHT_BLUE_DARK(
      R.style.AptoideThemeDark_LightBlue, false),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN(R.style.AptoideThemeLight_Green, false), SEA_GREEN_DARK(R.style.AptoideThemeDark_Green,
      false),

  //SLATEGRAY TO TEAL
  SLATE_GRAY(R.style.AptoideThemeLight_Teal, false), SLATE_GRAY_DARK(R.style.AptoideThemeDark_Teal,
      false),

  //BLUE TO INDIGO
  BLUE(R.style.AptoideThemeLight_Indigo, false), BLUE_DARK(R.style.AptoideThemeDark_Indigo, false),

  //MAROON TO BROWN
  MAROON(R.style.AptoideThemeLight_Brown, false), MAROON_DARK(R.style.AptoideThemeDark_Brown,
      false),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT(R.style.AptoideThemeLight_BlueGrey, false), MIDNIGHT_DARK(
      R.style.AptoideThemeDark_BlueGrey, false),

  BLUE_GRAY(R.style.AptoideThemeLight_BlueGrey, false), BLUE_GRAY_DARK(
      R.style.AptoideThemeDark_BlueGrey, false),

  //SILVER AND DIMGREY TO GREY
  SILVER(R.style.AptoideThemeLight_Grey, false), SILVER_DARK(R.style.AptoideThemeDark_Grey, false),

  DIM_GRAY(R.style.AptoideThemeLight_Grey, false), DIM_GRAY_DARK(R.style.AptoideThemeDark_Grey,
      false),

  //MAGENTA TO DEEPPURPLE
  MAGENTA(R.style.AptoideThemeLight_DeepPurple, false), MAGENTA_DARK(
      R.style.AptoideThemeDark_DeepPurple, false),

  //YELLOW AND GOLD TO AMBER
  YELLOW(R.style.AptoideThemeLight_Amber, false), YELLOW_DARK(R.style.AptoideThemeDark_Amber,
      false),

  GOLD(R.style.AptoideThemeLight_Amber, false), GOLD_DARK(R.style.AptoideThemeDark_Amber, false),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN(R.style.AptoideThemeLight_Green, false), SPRING_GREEN_DARK(
      R.style.AptoideThemeDark_Green, false),

  //GREENAPPLE TO LIME
  GREEN_APPLE(R.style.AptoideThemeLight_Lime, false), GREEN_APPLE_DARK(
      R.style.AptoideThemeDark_Lime, false),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY(R.style.AptoideThemeLight_LightBlue, false), LIGHT_SKY_DARK(
      R.style.AptoideThemeDark_LightBlue, false),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE(R.style.AptoideThemeLight_LightBlue, false), HAPPY_BLUE_DARK(
      R.style.AptoideThemeDark_LightBlue, false);

  @StyleRes private int storeStyle;
  private boolean isSelectable;

  StoreTheme(@StyleRes int storeStyle, boolean isSelectable) {
    this.storeStyle = storeStyle;
    this.isSelectable = isSelectable;
  }

  public static List<StoreTheme> getThemesFromVersion(int version) {
    List<StoreTheme> themes = new LinkedList<>();
    for (StoreTheme theme : values()) {
      if (theme.isSelectable) {
        themes.add(theme);
      }
    }
    return themes;
  }

  static StoreTheme get(String storeThemeName, boolean isDarkTheme) {
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

    if (isDarkTheme && !theme.toString()
        .contains("_DARK")) {
      theme = valueOf(theme.toString() + "_DARK");
    }

    return theme;
  }

  @StyleRes public int getThemeResource() {
    return storeStyle;
  }

  public String getThemeName() {
    String name = name().toLowerCase()
        .replace('_', '-');
    return name.contains("-dark") ? name.substring(0, name.indexOf("-dark")) : name;
  }
}
