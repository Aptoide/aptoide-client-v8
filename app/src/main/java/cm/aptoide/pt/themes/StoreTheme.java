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

  DEFAULT(R.style.AptoideThemeLight, 8), DEFAULT_DARK(R.style.AptoideThemeDark, 8),

  GREEN(R.style.AptoideThemeLight_Green, 8), GREEN_DARK(R.style.AptoideThemeDark_Green, 8),

  TEAL(R.style.AptoideThemeLight_Teal, 8), TEAL_DARK(R.style.AptoideThemeDark_Teal, 8),

  RED(R.style.AptoideThemeLight_Red, 8), RED_DARK(R.style.AptoideThemeDark_Red, 8),

  INDIGO(R.style.AptoideThemeLight_Indigo, 8), INDIGO_DARK(R.style.AptoideThemeDark_Indigo, 8),

  PINK(R.style.AptoideThemeLight_Pink, 8), PINK_DARK(R.style.AptoideThemeDark_Pink, 8),

  ORANGE(R.style.AptoideThemeLight_Orange, 8), ORANGE_DARK(R.style.AptoideThemeDark_Orange, 8),

  BROWN(R.style.AptoideThemeLight_Brown, 8), BROWN_DARK(R.style.AptoideThemeDark_Brown, 8),

  BLUE_GREY(R.style.AptoideThemeLight_BlueGrey, 8), BLUE_GREY_DARK(
      R.style.AptoideThemeDark_BlueGrey, 8),

  GREY(R.style.AptoideThemeLight_Grey, 8), GREY_DARK(R.style.AptoideThemeDark_Grey, 8),

  BLACK(R.style.AptoideThemeLight_Black, 8), BLACK_DARK(R.style.AptoideThemeDark_Black, 8),

  DEEP_PURPLE(R.style.AptoideThemeLight_DeepPurple, 8), DEEP_PURPLE_DARK(
      R.style.AptoideThemeDark_DeepPurple, 8),

  AMBER(R.style.AptoideThemeLight_Amber, 8), AMBER_DARK(R.style.AptoideThemeDark_Amber, 8),

  LIGHT_GREEN(R.style.AptoideThemeLight_LightGreen, 8), LIGHT_GREEN_DARK(
      R.style.AptoideThemeDark_LightGreen, 8),

  LIME(R.style.AptoideThemeLight_Lime, 8), LIME_DARK(R.style.AptoideThemeDark_Lime, 8),

  LIGHT_BLUE(R.style.AptoideThemeLight_LightBlue, 8), LIGHT_BLUE_DARK(
      R.style.AptoideThemeDark_LightBlue, 8),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN(R.style.AptoideThemeLight_Green, 7), SEA_GREEN_DARK(R.style.AptoideThemeDark_Green, 7),

  //SLATEGRAY TO TEAL
  SLATE_GRAY(R.style.AptoideThemeLight_Teal, 7), SLATE_GRAY_DARK(R.style.AptoideThemeDark_Teal, 7),

  //BLUE TO INDIGO
  BLUE(R.style.AptoideThemeLight_Indigo, 7), BLUE_DARK(R.style.AptoideThemeDark_Indigo, 7),

  //MAROON TO BROWN
  MAROON(R.style.AptoideThemeLight_Brown, 7), MAROON_DARK(R.style.AptoideThemeDark_Brown, 7),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT(R.style.AptoideThemeLight_BlueGrey, 7), MIDNIGHT_DARK(R.style.AptoideThemeDark_BlueGrey,
      7),

  BLUE_GRAY(R.style.AptoideThemeLight_BlueGrey, 7), BLUE_GRAY_DARK(
      R.style.AptoideThemeDark_BlueGrey, 7),

  //SILVER AND DIMGREY TO GREY
  SILVER(R.style.AptoideThemeLight_Grey, 7), SILVER_DARK(R.style.AptoideThemeDark_Grey, 7),

  DIM_GRAY(R.style.AptoideThemeLight_Grey, 7), DIM_GRAY_DARK(R.style.AptoideThemeDark_Grey, 7),

  //MAGENTA TO DEEPPURPLE
  MAGENTA(R.style.AptoideThemeLight_DeepPurple, 7), MAGENTA_DARK(
      R.style.AptoideThemeDark_DeepPurple, 7),

  //YELLOW AND GOLD TO AMBER
  YELLOW(R.style.AptoideThemeLight_Amber, 7), YELLOW_DARK(R.style.AptoideThemeDark_Amber, 7),

  GOLD(R.style.AptoideThemeLight_Amber, 7), GOLD_DARK(R.style.AptoideThemeDark_Amber, 7),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN(R.style.AptoideThemeLight_Green, 7), SPRING_GREEN_DARK(
      R.style.AptoideThemeDark_Green, 7),

  //GREENAPPLE TO LIME
  GREEN_APPLE(R.style.AptoideThemeLight_Lime, 7), GREEN_APPLE_DARK(R.style.AptoideThemeDark_Lime,
      7),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY(R.style.AptoideThemeLight_LightBlue, 7), LIGHT_SKY_DARK(
      R.style.AptoideThemeDark_LightBlue, 7),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE(R.style.AptoideThemeLight_LightBlue, 7), HAPPY_BLUE_DARK(
      R.style.AptoideThemeDark_LightBlue, 7);

  @StyleRes private int storeStyle;
  private int version;

  StoreTheme(@StyleRes int storeStyle, int version) {
    this.storeStyle = storeStyle;
    this.version = version;
  }

  public static List<StoreTheme> getThemesFromVersion(int version) {
    List<StoreTheme> themes = new LinkedList<>();
    for (StoreTheme theme : values()) {
      if (theme.getVersion() == version && theme.toString()
          .contains("DARK")) {
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

  public int getVersion() {
    return version;
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
