package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 28/10/2016.
 * This class will handle analytics events that are meant to be sent to our webservices to store.
 */

public class AptoideAnalytics {

  public static final String SOURCE_APTOIDE = "Aptoide";
  public static final String OPEN_ARTICLE = "Open_Article";
  public static final String OPEN_BLOG = "Open_Blog";
  public static final String OPEN_VIDEO = "Open_Video";
  public static final String OPEN_CHANNEL = "Open_Channel";
  public static final String OPEN_STORE = "Open_Store";
  public static final String OPEN_APP = "Open_App";
  public static final String UPDATE_APP = "Update_App";

  private AptoideAnalytics() {
    throw new IllegalStateException("You shall not instantiate this class!");
  }

  public static void logEvent(SendEventRequest.Body.Data data, String eventName) {
    SendEventRequest.of(AptoideAccountManager.getAccessToken(), data, eventName)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> {
        }, throwable -> throwable.printStackTrace());
  }
}
