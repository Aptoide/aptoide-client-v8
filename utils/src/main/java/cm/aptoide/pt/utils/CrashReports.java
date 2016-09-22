package cm.aptoide.pt.utils;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import lombok.Setter;

/**
 * Created by diogoloureiro on 16/09/16.
 */
public class CrashReports {

  private final static String TAG = CrashReports.class.getSimpleName();   //TAG for the logger
  @Setter private static String language;                                 //var with the language the app is set to
  private static boolean fabricConfigured;        //var if fabric is configured or not, true by default

  /**
   * setup crash reports
   * @param context context from the class that's calling this method
   *
   */
  public static void setup(Context context) {
    fabricConfigured = BuildConfig.FABRIC_CONFIGURED;
    Fabric.with(context, new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG || !fabricConfigured).build()).build());
    Logger.d(TAG,"Setup of CrashReports");
  }

  /**
   * logs exception in crashes
   * @param throwable exception you want to send
   */
  public static void logException(Throwable throwable) {
    Crashlytics.setString("Language", language);
    Crashlytics.logException(throwable);
    Logger.d(TAG, "logException: " + throwable.toString());
  }

  /**
   * logs string in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  public static void logString(String key, String value) {
    Crashlytics.setString(key, value);
    Logger.d(TAG, "logString : key: " + key + " , value: " + value);
  }

  /**
   * logs message in crashes
   * @param priority priority given to the message
   * @param tag unique tag that identifies the message
   * @param message message you want to send
   */
  public static void logMessage(int priority, String tag, String message) {
    Crashlytics.log(priority, tag, message);
    Logger.d(TAG, "logPriorityString: " + priority + " , " + tag + " , " + message);
  }

  /**
   * this class should have all the utils methods related to crashlytics
   */
  public static class ScreenUtils {
    static final String SCREEN_HISTORY = "SCREEN_HISTORY";
    static final String NUMBER_OF_SCREENS = "NUMBER_OF_SCREENS";
    static final String NUMBER_OF_SCREENS_ON_BACK_STACK = "NUMBER_OF_SCREENS_ON_BACK_STACK";
    static final String LIFE_CYCLE_STATE = "LIFE_CYCLE_STATE";

    private ArrayList<String> history = new ArrayList<>();   //arrayList with all screen names history
    private int MAX_HISTORY = 10;                            //this var sets the max screens should be added to history
    private int totalNumberScreens = 0;                      //This saves the number of screens showed
    private int numberScreensOnBackStack = 0;                //This is the current number of screens on the backstack
    public enum LifeCycle{CREATE,DESTROY}

    private ScreenUtils(){}

    private static class ScreenUtilsHelper{
      private static final ScreenUtils INSTANCE = new ScreenUtils();
    }

    /**
     * new instance of ScreenUtils
     * @return  instance of ScreenUtils
     */
    public static ScreenUtils getInstance(){
      return ScreenUtilsHelper.INSTANCE;
    }

    /**
     * this method adds a screen name to the history to be reported to crashReports
     * @param screenName screen name that should be reported to crashReports
     */
    public void addScreenToHistory(String screenName) {
      if (fabricConfigured) {
        if (history.size() >= MAX_HISTORY) {
          history.remove(0);
        }
        history.add(screenName);
        CrashReports.logString(SCREEN_HISTORY, history.toString());
        Logger.d(TAG,"addScreenToHistory: "+history.toString());
      }
    }

    /**
     * increment the number of screens, sends the number os total screens and the number of screens on backstack
     */
    public void incrementNumberOfScreens(){
      totalNumberScreens++;
      numberScreensOnBackStack++;
      CrashReports.logString(NUMBER_OF_SCREENS, String.valueOf(totalNumberScreens));
      CrashReports.logString(NUMBER_OF_SCREENS_ON_BACK_STACK, String.valueOf(numberScreensOnBackStack));
      CrashReports.logString(LIFE_CYCLE_STATE,LifeCycle.CREATE.toString());
      Logger.d(TAG,"incrementNumberOfScreens: NOS: "+NUMBER_OF_SCREENS+", NOSOBS: "+NUMBER_OF_SCREENS_ON_BACK_STACK);
    }

    /**
     * decrement the number of screens, sends the number of screens on backstack
     */
    public void decrementNumberOfScreens(){
      numberScreensOnBackStack--;
      CrashReports.logString(NUMBER_OF_SCREENS_ON_BACK_STACK, String.valueOf(numberScreensOnBackStack));
      CrashReports.logString(LIFE_CYCLE_STATE,LifeCycle.DESTROY.toString());
      Logger.d(TAG,"decrementNumberOfScreens: NOSOBS: "+String.valueOf(numberScreensOnBackStack));
    }
  }
}