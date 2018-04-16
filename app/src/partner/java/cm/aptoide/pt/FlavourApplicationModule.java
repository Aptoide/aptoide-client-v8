package cm.aptoide.pt;

import android.os.Environment;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.app.FirstInstallAnalytics;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallFabricEvents;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class FlavourApplicationModule {
  private AptoideApplication application;

  public FlavourApplicationModule(AptoideApplication application) {
    this.application = application;
  }

  @Singleton @Provides LoginPreferences provideLoginPreferences(BootConfig bootConfig) {
    return new LoginPreferences(bootConfig);
  }

  @Singleton @Provides @Named("extraID") String provideExtraID(BootConfig bootConfig) {
    return String.valueOf(bootConfig.getPartner()
        .getUid());
  }

  @Singleton @Provides @Named("marketName") String provideMarketName(BootConfig bootConfig) {
    return bootConfig.getPartner()
        .getStore()
        .getLabel();
  }

  @Singleton @Provides @Named("partners") ObjectMapper provideObjectMapper() {
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Singleton @Provides BootConfig provideBootConfig(@Named("partners") ObjectMapper objectMapper) {
    return BootConfigJSONUtils.getSavedRemoteBootConfig(application.getBaseContext(), objectMapper)
        .getData();
  }

  @Singleton @Provides @Named("defaultStoreName") String provideDefaultStoreName(
      BootConfig bootConfig) {
    return bootConfig.getPartner()
        .getStore()
        .getName();
  }

  @Singleton @Provides @Named("partnerID") String providePartnerID(BootConfig bootConfig) {
    return String.valueOf(bootConfig.getPartner()
        .getUid());
  }

  @Singleton @Provides @Named("accountType") String provideAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Singleton @Provides @Named("cachePath") String provideCachePath(
      @Named("defaultStoreName") String defaultStoreName) {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/." + defaultStoreName + "/";
  }

  @Singleton @Provides @Named("imageCachePath") String provideImageCachePatch(
      @Named("cachePath") String cachePath) {
    return cachePath + "/" + "icons/";
  }

  @Singleton @Provides @Named("flurryEvents") Collection<String> provideFlurryEvents() {
    List<String> flurryEvents = new LinkedList<>(Arrays.asList(InstallAnalytics.APPLICATION_INSTALL,
        DownloadAnalytics.EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT, AppViewAnalytics.HOME_PAGE_EDITORS_CHOICE_FLURRY,
        AppViewAnalytics.APP_VIEW_OPEN_FROM, StoreAnalytics.STORES_TAB_OPEN,
        StoreAnalytics.STORES_TAB_INTERACT, StoreAnalytics.STORES_OPEN,
        StoreAnalytics.STORES_INTERACT, AccountAnalytics.SIGN_UP_EVENT_NAME,
        AccountAnalytics.LOGIN_EVENT_NAME, FirstLaunchAnalytics.FIRST_LAUNCH,
        AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN, AccountAnalytics.CREATE_USER_PROFILE,
        AccountAnalytics.PROFILE_SETTINGS, AdultContentAnalytics.ADULT_CONTENT,
        AppViewAnalytics.DOWNGRADE_DIALOG, DeepLinkAnalytics.APP_LAUNCH,
        DeepLinkAnalytics.FACEBOOK_APP_LAUNCH, AppViewAnalytics.CLICK_INSTALL,
        FirstInstallAnalytics.FIRST_INSTALL_POP_UP,
        FirstInstallAnalytics.FIRST_INSTALL_CLOSE_WINDOW,
        FirstInstallAnalytics.FIRST_INSTALL_START_DOWNLOAD));
    for (CardType cardType : CardType.values()) {
      flurryEvents.add(cardType.name() + "_" + TimelineAnalytics.APPS_TIMELINE_EVENT);
    }
    return flurryEvents;
  }

  @Singleton @Provides @Named("facebookEvents") Collection<String> provideFacebookEvents() {
    return Arrays.asList(PostAnalytics.OPEN_EVENT_NAME, PostAnalytics.NEW_POST_EVENT_NAME,
        PostAnalytics.POST_COMPLETE, InstallAnalytics.APPLICATION_INSTALL,
        InstallAnalytics.NOTIFICATION_APPLICATION_INSTALL,
        InstallAnalytics.EDITORS_APPLICATION_INSTALL,
        AddressBookAnalytics.FOLLOW_FRIENDS_CHOOSE_NETWORK,
        AddressBookAnalytics.FOLLOW_FRIENDS_HOW_TO,
        AddressBookAnalytics.FOLLOW_FRIENDS_APTOIDE_ACCESS,
        AddressBookAnalytics.FOLLOW_FRIENDS_NEW_CONNECTIONS,
        AddressBookAnalytics.FOLLOW_FRIENDS_SET_MY_PHONENUMBER,
        DownloadAnalytics.EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT, SearchAnalytics.SEARCH,
        SearchAnalytics.NO_RESULTS, SearchAnalytics.APP_CLICK, SearchAnalytics.SEARCH_START,
        AppViewAnalytics.EDITORS_CHOICE_CLICKS, AppViewAnalytics.APP_VIEW_OPEN_FROM,
        AppViewAnalytics.APP_VIEW_INTERACT, NotificationAnalytics.NOTIFICATION_RECEIVED,
        NotificationAnalytics.NOTIFICATION_PRESSED, NotificationAnalytics.NOTIFICATION_RECEIVED,
        TimelineAnalytics.SOCIAL_CARD_PREVIEW, TimelineAnalytics.CARD_ACTION,
        TimelineAnalytics.TIMELINE_OPENED, TimelineAnalytics.FOLLOW_FRIENDS,
        StoreAnalytics.STORES_TAB_OPEN, StoreAnalytics.STORES_TAB_INTERACT,
        StoreAnalytics.STORES_OPEN, StoreAnalytics.STORES_INTERACT,
        AccountAnalytics.SIGN_UP_EVENT_NAME, AccountAnalytics.LOGIN_EVENT_NAME,
        UpdatesAnalytics.UPDATE_EVENT, PageViewsAnalytics.PAGE_VIEW_EVENT,
        DrawerAnalytics.DRAWER_OPEN_EVENT, DrawerAnalytics.DRAWER_INTERACT_EVENT,
        FirstLaunchAnalytics.FIRST_LAUNCH, InstallFabricEvents.ROOT_V2_COMPLETE,
        InstallFabricEvents.ROOT_V2_START, AppViewSimilarAppAnalytics.APP_VIEW_SIMILAR_APP_SLIDE_IN,
        AppViewSimilarAppAnalytics.SIMILAR_APP_INTERACT,
        NotLoggedInShareAnalytics.POP_UP_SHARE_TIMELINE,
        AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN, AccountAnalytics.CREATE_USER_PROFILE,
        AccountAnalytics.PROFILE_SETTINGS, AccountAnalytics.ENTRY,
        DeepLinkAnalytics.FACEBOOK_APP_LAUNCH, AppViewAnalytics.CLICK_INSTALL,
        BillingAnalytics.PAYMENT_AUTH, BillingAnalytics.PAYMENT_LOGIN,
        BillingAnalytics.PAYMENT_POPUP, FirstInstallAnalytics.FIRST_INSTALL_START_DOWNLOAD,
        FirstInstallAnalytics.FIRST_INSTALL_POP_UP,
        FirstInstallAnalytics.FIRST_INSTALL_CLOSE_WINDOW);
  }
}
