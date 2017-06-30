package cm.aptoide.pt.utils;

import java.util.Locale;

/**
 * Created by danielchen on 06/06/17.
 */

public class LanguageUtils {

  /**
   * This method returns a new Locale. If the string received as a parameter has a "-" or "_", it is
   * split in two (left side is the language and the right side the country)
   *
   * @param locale - String to be converted to Locale (e.g. "pt-BR", "en", etc...)
   * @return new Locale
   */
  public static Locale getLocaleFromString(String locale) {
    String[] s;
    if (locale.contains("_")) {
      s = locale.split("_");
      if (s.length > 1) {
        return new Locale(s[0], s[1]);
      } else if (s.length == 1) {
        return new Locale(s[0]);
      }
    }
    return new Locale(locale);
  }

  /**
   * This method uses the received Locale object and gets it's country and language
   * The string's first char is written in upper case for display purposes
   *
   * @param locale - Locale used to fetch it's country and language
   * @return new String (e.g. "Português", "Português (Brasil)", "English", etc...)
   */
  public static String displayCountryAndLanguage(Locale locale) {
    String country = locale.getDisplayCountry(locale);
    String language = locale.getDisplayLanguage(locale);
    String formattedStringToRtn;

    //Check if is Hebrew or Indonesian (the country code changed, check https://developer.android.com/reference/java/util/Locale.html)
    if (locale.getCountry().equalsIgnoreCase(locale.getLanguage()) || locale.getLanguage()
        .equals(new Locale("he").getLanguage()) || locale.getLanguage()
        .equals(new Locale("id").getLanguage())) {
      formattedStringToRtn = language;
    } else {
      formattedStringToRtn = language + " (" + country + ")";
    }

    if (formattedStringToRtn.length() > 0) {
      formattedStringToRtn =
          formattedStringToRtn.substring(0, 1).toUpperCase() + formattedStringToRtn.substring(1);
    }
    return formattedStringToRtn;
  }
}
