package cm.aptoide.pt.addressbook;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class AddressBookAnalytics {

  public static final String FOLLOW_FRIENDS_SET_MY_PHONENUMBER =
      "Follow_Friends_Set_My_Phonenumber";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public AddressBookAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendShareYourPhoneSuccessEvent() {
    analyticsManager.logEvent(null, FOLLOW_FRIENDS_SET_MY_PHONENUMBER,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
