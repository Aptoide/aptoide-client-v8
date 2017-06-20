package cm.aptoide.pt.utils;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by danielchen on 06/06/17.
 */

public class LanguageUtils {

    /**
     * This method returns a new Locale. If the string received as a parameter has a "-" or "_", it is split in two (left side is the language and the right side the country)
     *
     * @param locale - String to be converted to Locale (e.g. "pt-BR", "en", etc...)
     * @return new Locale
     */
    public static Locale getLocaleFromString(String locale){
        String[] s ;
        if(locale.contains("-")){
            s = locale.split("-");
            return new Locale(s[0], s[1]);
        }else{
            if(locale.contains("_")){
                s = locale.split("_");
                return new Locale(s[0], s[1]);
            }else{
                return new Locale(locale);
            }
        }

    }

    /**
     * This method uses the received Locale object and gets it's country and language
     * If getDisplayCountry returns empty its because it was not set, and thus the method will return String with the language
     * If getDisplayCountry does not return empty, the method will return a String with both language and country
     * The string's first char is written in upper case for display purposes
     *
     * @param locale - Locale used to fetch it's country and language
     * @return new String (e.g. "Português", "Português (Brasil)", "English", etc...)
     */
    public static String getCountryAndLanguageStringCaps(Locale locale){
        String country = locale.getDisplayCountry(locale);
        String language = locale.getDisplayLanguage(locale);
        String formattedStringToRtn;

        if(TextUtils.isEmpty(country)){
            formattedStringToRtn = language;
        }else{
            formattedStringToRtn = language + " (" + country + ")";
        }
        if(formattedStringToRtn.length()>0){
            formattedStringToRtn = formattedStringToRtn.substring(0,1).toUpperCase() + formattedStringToRtn.substring(1);
        }
        return formattedStringToRtn;
    }
}
