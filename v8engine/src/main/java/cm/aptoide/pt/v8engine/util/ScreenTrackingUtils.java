package cm.aptoide.pt.v8engine.util;

import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;

/**
 * this class should have all the utils methods related to crashlytics
 */
public class ScreenTrackingUtils {
  static final String SCREEN_HISTORY = "SCREEN_HISTORY";
  static final String NUMBER_OF_SCREENS = "NUMBER_OF_SCREENS";
  static final String NUMBER_OF_SCREENS_ON_BACK_STACK = "NUMBER_OF_SCREENS_ON_BACK_STACK";
  static final String LIFE_CYCLE_STATE = "LIFE_CYCLE_STATE";
  private static final String TAG = ScreenTrackingUtils.class.getSimpleName();
  private ArrayList<String> history = new ArrayList<>();
  //arrayList with all screen names history
  private int MAX_HISTORY = 10;
  //this var sets the max screens should be added to history
  private int totalNumberScreens = 0;
  //This saves the number of screens showed
  private int numberScreensOnBackStack = 0;
  //This is the current number of screens on the backstack

  private ScreenTrackingUtils() {
  }

  /**
   * new instance of ScreenTrackingUtils
   *
   * @return instance of ScreenTrackingUtils
   */
  public static ScreenTrackingUtils getInstance() {
    return ScreenUtilsHelper.INSTANCE;
  }

  /**
   * this method adds a screen name to the history to be reported to crashReports
   *
   * @param screenName screen name that should be reported to crashReports
   */
  public void addScreenToHistory(String screenName) {
    if (history.size() >= MAX_HISTORY) {
      history.remove(0);
    }
    history.add(screenName);
    CrashReport.getInstance().log(SCREEN_HISTORY, history.toString());
    Logger.d(TAG, "addScreenToHistory: " + history.toString());
  }

  /**
   * increment the number of screens, sends the number os total screens and the number of screens
   * on backstack
   */
  public void incrementNumberOfScreens() {
    totalNumberScreens++;
    numberScreensOnBackStack++;
    CrashReport.getInstance().log(NUMBER_OF_SCREENS, String.valueOf(totalNumberScreens));
    CrashReport.getInstance()
        .log(NUMBER_OF_SCREENS_ON_BACK_STACK, String.valueOf(numberScreensOnBackStack));
    CrashReport.getInstance().log(LIFE_CYCLE_STATE, LifeCycle.CREATE.toString());
    Logger.d(TAG, "incrementNumberOfScreens: NOS: "
        + NUMBER_OF_SCREENS
        + ", NOSOBS: "
        + NUMBER_OF_SCREENS_ON_BACK_STACK);
  }

  /**
   * decrement the number of screens, sends the number of screens on backstack
   */
  public void decrementNumberOfScreens() {
    numberScreensOnBackStack--;
    CrashReport.getInstance()
        .log(NUMBER_OF_SCREENS_ON_BACK_STACK, String.valueOf(numberScreensOnBackStack));
    CrashReport.getInstance().log(LIFE_CYCLE_STATE, LifeCycle.DESTROY.toString());
    Logger.d(TAG, "decrementNumberOfScreens: NOSOBS: " + String.valueOf(numberScreensOnBackStack));
  }

  public enum LifeCycle {CREATE, DESTROY}

  private static class ScreenUtilsHelper {
    private static final ScreenTrackingUtils INSTANCE = new ScreenTrackingUtils();
  }
}
