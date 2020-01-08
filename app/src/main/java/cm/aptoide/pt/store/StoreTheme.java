/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.store;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

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
      R.color.default_text, R.drawable.default_search_button_background,
      R.drawable.aptoide_gradient_rounded, R.style.DefaultDatePickerDialog, 8),

  GREEN(R.style.AptoideThemeDefaultGreen_Light, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.green_gradient, R.color.green_text, R.drawable.green_search_button_background,
      R.drawable.green_gradient_rounded, R.style.GreenDatePickerDialog, 8),

  TEAL(R.style.AptoideThemeDefaultTeal_Light, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.teal_search_button_background,
      R.drawable.teal_gradient_rounded, R.style.TealDatePickerDialog, 8),

  RED(R.style.AptoideThemeDefaultRed_Light, R.color.red, R.color.red_gradient_end,
      R.drawable.button_border_red, R.drawable.create_store_theme_shape_red,
      R.drawable.red_gradient, R.color.red_text, R.drawable.red_search_button_background,
      R.drawable.red_gradient_rounded, R.style.RedDatePickerDialog, 8),

  INDIGO(R.style.AptoideThemeDefaultIndigo_Light, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.indigo_text, R.drawable.indigo_search_button_background,
      R.drawable.indigo_gradient_rounded, R.style.IndigoDatePickerDialog, 8),

  PINK(R.style.AptoideThemeDefaultPink_Light, R.color.pink, R.color.pink_gradient_end,
      R.drawable.button_border_pink, R.drawable.create_store_theme_shape_pink,
      R.drawable.pink_gradient, R.color.pink_text, R.drawable.pink_search_button_background,
      R.drawable.pink_gradient_rounded, R.style.PinkDatePickerDialog, 8),

  ORANGE(R.style.AptoideThemeDefaultOrange_Light, R.color.orange, R.color.orange_gradient_end,
      R.drawable.button_border_orange, R.drawable.create_store_theme_shape_orange,
      R.drawable.orange_gradient, R.color.orange_text, R.drawable.orange_search_button_background,
      R.drawable.orange_gradient_rounded, R.style.OrangeDatePickerDialog, 8),

  BROWN(R.style.AptoideThemeDefaultBrown_Light, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.brown_search_button_background,
      R.drawable.brown_gradient_rounded, R.style.BrownDatePickerDialog, 8),

  BLUE_GREY(R.style.AptoideThemeDefaultBluegrey_Light, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.blue_grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 8),

  GREY(R.style.AptoideThemeDefaultGrey_Light, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 8),

  BLACK(R.style.AptoideThemeDefaultBlack_Light, R.color.black, R.color.grey,
      R.drawable.button_border_black, R.drawable.create_store_theme_shape_black,
      R.drawable.black_gradient, R.color.black_text, R.drawable.black_search_button_background,
      R.drawable.black_gradient_rounded, R.style.BlackDatePickerDialog, 8),

  DEEP_PURPLE(R.style.AptoideThemeDefaultDeepPurple_Light, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.deep_purple_search_button_background,
      R.drawable.deep_purple_gradient_rounded, R.style.DeepPurpleDatePickerDialog, 8),

  AMBER(R.style.AptoideThemeDefaultAmber_Light, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 8),

  LIGHT_GREEN(R.style.AptoideThemeDefaultLightgreen_Light, R.color.light_green,
      R.color.light_green_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.light_green_search_button_background,
      R.drawable.light_green_gradient_rounded, R.style.LightGreenDatePickerDialog, 8),

  LIME(R.style.AptoideThemeDefaultLime_Light, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.lime_search_button_background,
      R.drawable.lime_gradient_rounded, R.style.LimeDatePickerDialog, 8),

  LIGHT_BLUE(R.style.AptoideThemeDefaultLightblue_Light, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 8),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN(R.style.AptoideThemeDefaultGreen_Light, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.light_green_gradient, R.color.light_green_text,
      R.drawable.green_search_button_background, R.drawable.light_green_gradient_rounded,
      R.style.GreenDatePickerDialog, 7),

  //SLATEGRAY TO TEAL
  SLATE_GRAY(R.style.AptoideThemeDefaultTeal_Light, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.teal_search_button_background,
      R.drawable.teal_gradient_rounded, R.style.TealDatePickerDialog, 7),

  //BLUE TO INDIGO
  BLUE(R.style.AptoideThemeDefaultIndigo_Light, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.blue_grey_text,
      R.drawable.indigo_search_button_background, R.drawable.indigo_gradient_rounded,
      R.style.IndigoDatePickerDialog, 7),

  //MAROON TO BROWN
  MAROON(R.style.AptoideThemeDefaultBrown_Light, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.brown_search_button_background,
      R.drawable.brown_gradient_rounded, R.style.BrownDatePickerDialog, 7),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT(R.style.AptoideThemeDefaultBluegrey_Light, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 7),

  BLUE_GRAY(R.style.AptoideThemeDefaultBluegrey_Light, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.blue_grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 7),

  //SILVER AND DIMGREY TO GREY
  SILVER(R.style.AptoideThemeDefaultGrey_Light, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 7),

  DIM_GRAY(R.style.AptoideThemeDefaultGrey_Light, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 7),

  //MAGENTA TO DEEPPURPLE
  MAGENTA(R.style.AptoideThemeDefaultDeepPurple_Light, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.deep_purple_search_button_background,
      R.drawable.deep_purple_gradient_rounded, R.style.DeepPurpleDatePickerDialog, 7),

  //YELLOW AND GOLD TO AMBER
  YELLOW(R.style.AptoideThemeDefaultAmber_Light, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 7),

  GOLD(R.style.AptoideThemeDefaultAmber_Light, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 7),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN(R.style.AptoideThemeDefaultLightgreen_Light, R.color.light_green,
      R.color.light_green_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.light_green_search_button_background,
      R.drawable.light_green_gradient_rounded, R.style.LightGreenDatePickerDialog, 7),

  //GREENAPPLE TO LIME
  GREEN_APPLE(R.style.AptoideThemeDefaultLime_Light, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.lime_search_button_background,
      R.drawable.lime_gradient_rounded, R.style.LimeDatePickerDialog, 7),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY(R.style.AptoideThemeDefaultLightblue_Light, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 7),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE(R.style.AptoideThemeDefaultLightblue_Light, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 7),

  //DARK THEME

  DEFAULT_DARK(R.style.AptoideThemeDefaultDark, R.color.default_orange_gradient_start,
      R.color.default_orange_gradient_end, R.drawable.button_border_orange,
      R.drawable.create_store_theme_shape_default, R.drawable.main_orange_gradient,
      R.color.default_text, R.drawable.default_search_button_background,
      R.drawable.aptoide_gradient_rounded, R.style.DefaultDatePickerDialog, 8),

  GREEN_DARK(R.style.AptoideThemeDefaultGreen_Dark, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.green_gradient, R.color.green_text, R.drawable.green_search_button_background,
      R.drawable.green_gradient_rounded, R.style.GreenDatePickerDialog, 8),

  TEAL_DARK(R.style.AptoideThemeDefaultTeal_Dark, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.teal_search_button_background,
      R.drawable.teal_gradient_rounded, R.style.TealDatePickerDialog, 8),

  RED_DARK(R.style.AptoideThemeDefaultRed_Dark, R.color.red, R.color.red_gradient_end,
      R.drawable.button_border_red, R.drawable.create_store_theme_shape_red,
      R.drawable.red_gradient, R.color.red_text, R.drawable.red_search_button_background,
      R.drawable.red_gradient_rounded, R.style.RedDatePickerDialog, 8),

  INDIGO_DARK(R.style.AptoideThemeDefaultIndigo_Dark, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.indigo_text, R.drawable.indigo_search_button_background,
      R.drawable.indigo_gradient_rounded, R.style.IndigoDatePickerDialog, 8),

  PINK_DARK(R.style.AptoideThemeDefaultPink_Dark, R.color.pink, R.color.pink_gradient_end,
      R.drawable.button_border_pink, R.drawable.create_store_theme_shape_pink,
      R.drawable.pink_gradient, R.color.pink_text, R.drawable.pink_search_button_background,
      R.drawable.pink_gradient_rounded, R.style.PinkDatePickerDialog, 8),

  ORANGE_DARK(R.style.AptoideThemeDefaultOrange_Dark, R.color.orange, R.color.orange_gradient_end,
      R.drawable.button_border_orange, R.drawable.create_store_theme_shape_orange,
      R.drawable.orange_gradient, R.color.orange_text, R.drawable.orange_search_button_background,
      R.drawable.orange_gradient_rounded, R.style.OrangeDatePickerDialog, 8),

  BROWN_DARK(R.style.AptoideThemeDefaultBrown_Dark, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.brown_search_button_background,
      R.drawable.brown_gradient_rounded, R.style.BrownDatePickerDialog, 8),

  BLUE_GREY_DARK(R.style.AptoideThemeDefaultBluegrey_Dark, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.blue_grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 8),

  GREY_DARK(R.style.AptoideThemeDefaultGrey_Dark, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 8),

  BLACK_DARK(R.style.AptoideThemeDefaultBlack_Dark, R.color.black, R.color.grey,
      R.drawable.button_border_black, R.drawable.create_store_theme_shape_black,
      R.drawable.black_gradient, R.color.black_text, R.drawable.black_search_button_background,
      R.drawable.black_gradient_rounded, R.style.BlackDatePickerDialog, 8),

  DEEP_PURPLE_DARK(R.style.AptoideThemeDefaultDeepPurple_Dark, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.deep_purple_search_button_background,
      R.drawable.deep_purple_gradient_rounded, R.style.DeepPurpleDatePickerDialog, 8),

  AMBER_DARK(R.style.AptoideThemeDefaultAmber_Dark, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 8),

  LIGHT_GREEN_DARK(R.style.AptoideThemeDefaultLightgreen_Dark, R.color.light_green,
      R.color.light_green_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.light_green_search_button_background,
      R.drawable.light_green_gradient_rounded, R.style.LightGreenDatePickerDialog, 8),

  LIME_DARK(R.style.AptoideThemeDefaultLime_Dark, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.lime_search_button_background,
      R.drawable.lime_gradient_rounded, R.style.LimeDatePickerDialog, 8),

  LIGHT_BLUE_DARK(R.style.AptoideThemeDefaultLightblue_Dark, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 8),

  //Translated themes to new version
  //SEAGREEN TO GREEN
  SEA_GREEN_DARK(R.style.AptoideThemeDefaultGreen_Dark, R.color.green, R.color.green_gradient_end,
      R.drawable.button_border_green, R.drawable.create_store_theme_shape_green,
      R.drawable.light_green_gradient, R.color.light_green_text,
      R.drawable.green_search_button_background, R.drawable.light_green_gradient_rounded,
      R.style.GreenDatePickerDialog, 7),

  //SLATEGRAY TO TEAL
  SLATE_GRAY_DARK(R.style.AptoideThemeDefaultTeal_Dark, R.color.teal, R.color.teal_gradient_end,
      R.drawable.button_border_teal, R.drawable.create_store_theme_shape_teal,
      R.drawable.teal_gradient, R.color.teal_text, R.drawable.teal_search_button_background,
      R.drawable.teal_gradient_rounded, R.style.TealDatePickerDialog, 7),

  //BLUE TO INDIGO
  BLUE_DARK(R.style.AptoideThemeDefaultIndigo_Dark, R.color.indigo, R.color.indigo_gradient_end,
      R.drawable.button_border_indigo, R.drawable.create_store_theme_shape_indigo,
      R.drawable.indigo_gradient, R.color.blue_grey_text,
      R.drawable.indigo_search_button_background, R.drawable.indigo_gradient_rounded,
      R.style.IndigoDatePickerDialog, 7),

  //MAROON TO BROWN
  MAROON_DARK(R.style.AptoideThemeDefaultBrown_Dark, R.color.brown, R.color.brown_gradient_end,
      R.drawable.button_border_brown, R.drawable.create_store_theme_shape_brown,
      R.drawable.brown_gradient, R.color.brown_text, R.drawable.brown_search_button_background,
      R.drawable.brown_gradient_rounded, R.style.BrownDatePickerDialog, 7),

  //MIDNIGHT TO BLUE_GREY
  MIDNIGHT_DARK(R.style.AptoideThemeDefaultBluegrey_Dark, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 7),

  BLUE_GRAY_DARK(R.style.AptoideThemeDefaultBluegrey_Dark, R.color.blue_grey,
      R.color.blue_grey_gradient_end, R.drawable.button_border_bluegrey,
      R.drawable.create_store_theme_shape_blue_grey, R.drawable.blue_grey_gradient,
      R.color.blue_grey_text, R.drawable.blue_grey_search_button_background,
      R.drawable.blue_grey_gradient_rounded, R.style.BlueGreyDatePickerDialog, 7),

  //SILVER AND DIMGREY TO GREY
  SILVER_DARK(R.style.AptoideThemeDefaultGrey_Dark, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 7),

  DIM_GRAY_DARK(R.style.AptoideThemeDefaultGrey_Dark, R.color.grey, R.color.grey_gradient_end,
      R.drawable.button_border_grey, R.drawable.create_store_theme_shape_grey,
      R.drawable.grey_gradient, R.color.grey_text, R.drawable.grey_search_button_background,
      R.drawable.grey_gradient_rounded, R.style.GreyDatePickerDialog, 7),

  //MAGENTA TO DEEPPURPLE
  MAGENTA_DARK(R.style.AptoideThemeDefaultDeepPurple_Dark, R.color.deep_purple,
      R.color.deep_purple_gradient_end, R.drawable.button_border_deeppurple,
      R.drawable.create_store_theme_shape_deep_purple, R.drawable.deep_purple_gradient,
      R.color.deep_purple_text, R.drawable.deep_purple_search_button_background,
      R.drawable.deep_purple_gradient_rounded, R.style.DeepPurpleDatePickerDialog, 7),

  //YELLOW AND GOLD TO AMBER
  YELLOW_DARK(R.style.AptoideThemeDefaultAmber_Dark, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 7),

  GOLD_DARK(R.style.AptoideThemeDefaultAmber_Dark, R.color.amber, R.color.amber_gradient_end,
      R.drawable.button_border_amber, R.drawable.create_store_theme_shape_amber,
      R.drawable.amber_gradient, R.color.amber_text, R.drawable.amber_search_button_background,
      R.drawable.amber_gradient_rounded, R.style.AmberDatePickerDialog, 7),

  //SPRINGGREEN TO LIGHTGREEN
  SPRING_GREEN_DARK(R.style.AptoideThemeDefaultLightgreen_Dark, R.color.light_green,
      R.color.light_green_gradient_end, R.drawable.button_border_lightgreen,
      R.drawable.create_store_theme_shape_light_green, R.drawable.light_green_gradient,
      R.color.light_green_text, R.drawable.light_green_search_button_background,
      R.drawable.light_green_gradient_rounded, R.style.LightGreenDatePickerDialog, 7),

  //GREENAPPLE TO LIME
  GREEN_APPLE_DARK(R.style.AptoideThemeDefaultLime_Dark, R.color.lime, R.color.lime_gradient_end,
      R.drawable.button_border_lime, R.drawable.create_store_theme_shape_lime,
      R.drawable.lime_gradient, R.color.lime_text, R.drawable.lime_search_button_background,
      R.drawable.lime_gradient_rounded, R.style.LimeDatePickerDialog, 7),

  //LIGHTSKY AND LIGHTSKY TO LIGHTBLUE
  LIGHT_SKY_DARK(R.style.AptoideThemeDefaultLightblue_Dark, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 7),

  //LIGHTSKY AND HAPPYBLUE TO LIGHTBLUE
  HAPPY_BLUE_DARK(R.style.AptoideThemeDefaultLightblue_Dark, R.color.light_blue,
      R.color.light_blue_gradient_end, R.drawable.button_border_lightblue,
      R.drawable.create_store_theme_shape_lightblue, R.drawable.light_blue_gradient,
      R.color.light_blue_text, R.drawable.light_blue_search_button_background,
      R.drawable.light_blue_gradient_rounded, R.style.LightBlueDatePickerDialog, 7);

  @ColorRes private final int darkerColor;
  @DrawableRes private final int buttonDrawable;
  @DrawableRes private final int raisedButtonDrawable;
  @DrawableRes private final int roundDrawable;
  @ColorRes private final int colorLetters;
  @StyleRes private final int datePickerStyle;
  @StyleRes private int storeStyle;
  @ColorRes private int primaryColor;
  @DrawableRes private int gradientDrawable;
  @DrawableRes private int roundGradientButtonDrawable;
  private int version;

  StoreTheme(@StyleRes int storeStyle, @ColorRes int primaryColor, @ColorRes int darkerColor,
      @DrawableRes int buttonDrawable, @DrawableRes int roundDrawable,
      @DrawableRes int gradientDrawable, int colorLetters, int roundGradientButtonDrawable,
      @DrawableRes int raisedButtonDrawable, @StyleRes int datePickerStyle, int version) {
    this.storeStyle = storeStyle;
    this.primaryColor = primaryColor;
    this.darkerColor = darkerColor;
    this.buttonDrawable = buttonDrawable;
    this.roundDrawable = roundDrawable;
    this.gradientDrawable = gradientDrawable;
    this.colorLetters = colorLetters;
    this.roundGradientButtonDrawable = roundGradientButtonDrawable;
    this.raisedButtonDrawable = raisedButtonDrawable;
    this.datePickerStyle = datePickerStyle;
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
        .getTheme(), false);
  }

  public static StoreTheme get(String storeThemeName, boolean isDarkTheme) {
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

  //TODO: Remove
  @NotNull public static StoreTheme get(@NotNull String theme, boolean hasDarkMode) {
    return null;
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

  public int getRaisedButtonDrawable() {
    return raisedButtonDrawable;
  }

  public int getDatePickerStyle() {
    return datePickerStyle;
  }
}
