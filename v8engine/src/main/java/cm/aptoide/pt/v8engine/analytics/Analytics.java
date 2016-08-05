package cm.aptoide.pt.v8engine.analytics;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipFile;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;

import com.flurry.android.FlurryAgent;
import com.localytics.android.Localytics;

/**
 * Created by neuro on 07-05-2015.f
 * v8 integration by jdandrade on 25-07-2016
 */
public class Analytics {

    private static final boolean ACTIVATE = BuildConfig.LOCALYTICS_CONFIGURED;
    private static final boolean ACTIVATE_FLURRY = BuildConfig.FLURRY_CONFIGURED;
    private static final int ALL = Integer.MAX_VALUE;
    private static final int LOCALYTICS = 1 << 0;

    // Constantes globais a todos os eventos.
    public static final String ACTION = "Action";

    /**
     * Verifica se as flags fornecidas constam em accepted.
     *
     * @param flag     flags fornecidas
     * @param accepted flags aceitáveis
     * @return true caso as flags fornecidas constem em accepted.
     */
    private static boolean checkAcceptability(int flag, int accepted) {
        return (flag & accepted) == accepted;
    }

    private static void track(String event, String key, String attr, int flags) {

        try {
            if (!ACTIVATE)
                return;

            HashMap stringObjectHashMap = new HashMap<>();

            stringObjectHashMap.put(key, attr);

            track(event, stringObjectHashMap, flags);

            Logger.d("Analytics", "Event: " + event + ", Key: " + key + ", attr: " + attr);

        } catch (Exception e) {
            Log.d("Analytics", e.getStackTrace().toString());
        }

    }

    private static void track(String event, HashMap map, int flags) {
        try {
            if (!ACTIVATE){
                Logger.d(Analytics.class.getName(), "Localytics ACTIVATE value: " + ACTIVATE);

                return;
            }
            if(checkAcceptability(flags, LOCALYTICS))
                Localytics.tagEvent(event, map);

            Logger.d("Analytics", "Event: " + event + ", Map: " + map);

        } catch (Exception e) {
            Log.d("Analytics", e.getStackTrace().toString());
        }
    }

    private static void track(String event, int flags) {

        try {
            if (!ACTIVATE)
                return;

            if(checkAcceptability(flags, LOCALYTICS))
                Localytics.tagEvent(event);

            Logger.d("Analytics", "Event: " + event);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class Lifecycle {

        public static class Application {
            public static void onCreate(android.app.Application application) {

                if (!ACTIVATE)
                    return;

                // Integrate Localytics
                Localytics.autoIntegrate(application);
                checkForUTMFileInMetaINF();

            }

            private static void checkForUTMFileInMetaINF() {
                ZipFile myZipFile = null;
                try {
                    final String sourceDir = V8Engine.getContext().getPackageManager().getPackageInfo(V8Engine.getContext().getPackageName(), 0).applicationInfo
                            .sourceDir;
                    myZipFile = new ZipFile(sourceDir);
                    final InputStream utmInputStream = myZipFile.getInputStream(myZipFile.getEntry("META-INF/utm"));

                    UTMFileParser utmFileParser = new UTMFileParser(utmInputStream);
                    myZipFile.close();

                    Logger.d(Lifecycle.class.getName(), UTMFileParser.UTM_SOURCE + " : " + utmFileParser.valueExtracter(UTMFileParser.UTM_SOURCE));
                    Logger.d(Lifecycle.class.getName(), UTMFileParser.UTM_MEDIUM + " : " + utmFileParser.valueExtracter(UTMFileParser.UTM_MEDIUM));
                    Logger.d(Lifecycle.class.getName(), UTMFileParser.UTM_CAMPAIGN + " : " + utmFileParser.valueExtracter(UTMFileParser.UTM_CAMPAIGN));
                    Logger.d(Lifecycle.class.getName(), UTMFileParser.UTM_CONTENT + " : " + utmFileParser.valueExtracter(UTMFileParser.UTM_CONTENT));

                    //TODO set dimensions according to UTM file variables      no UTM value = null so we need to check for that

                    utmInputStream.close();
                } catch (IOException e) {
                    Logger.d(Analytics.class.getName(), "problem parsing utm/no utm file");
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.d(Analytics.class.getName(), "No package name utm file.");
                } catch (NullPointerException e){
                    if(myZipFile != null) {
                        try {
                            myZipFile.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Logger.d(Analytics.class.getName(), "No utm file.");
                }
            }
        }

        public static class Activity {

            private static final String TAG = Activity.class.getName();

            public static void onCreate(android.app.Activity activity) {

                if (!ACTIVATE_FLURRY)
                    return;

            }

            public static void onDestroy(android.app.Activity activity) {

                if (!ACTIVATE)
                    return;

            }

            public static void onResume(android.app.Activity activity) {

                if(!ACTIVATE)
                    return;

                Localytics.onActivityResume(activity);

            }

            public static void onPause(android.app.Activity activity){
                Localytics.onActivityPaused(activity);
            }


            public static void onStart(android.app.Activity activity) {

                if (!ACTIVATE_FLURRY)
                    return;

                Logger.d(TAG, "FlurryAgent.onStartSession called");
                FlurryAgent.onStartSession(activity, BuildConfig.FLURRY_KEY);

            }

            public static void onStop(android.app.Activity activity) {

                if (!ACTIVATE_FLURRY)
                    return;

                Logger.d(TAG, "FlurryAgent.onEndSession called");
                FlurryAgent.onEndSession(activity);

            }
        }

    }

    public static class Screens {

        public static void tagScreen(String screenName) {

            if (!ACTIVATE)
                return;

            Logger.d("Analytics", "Localytics: Screens: " + screenName);

            Localytics.tagScreen(screenName);
            Localytics.upload();
        }
    }

    // TODO
    public static class Tutorial {
        public static final String EVENT_NAME = "Tutorial";
        public static final String STEP_ACCOMPLISHED = "Step Accomplished";

        public static void finishedTutorial(int lastFragment) {
            try {
                track(EVENT_NAME, STEP_ACCOMPLISHED, Integer.toString(lastFragment), ALL);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class UserRegister {

        public static final String EVENT_NAME = "User Registered";

        public static void registered() {
            track(EVENT_NAME, ALL);
        }
    }

    // Novos
    public static class Rollback {

        private static final String EVENT_NAME = "Rollback";
        private static final String DOWNGRADED = "Downgraded";
        private static final String CLEAR = "Clear";

        public static void downgraded() {
//            track(EVENT_NAME, ACTION, DOWNGRADED, ALL);
        }

        public static void clear() {
//            track(EVENT_NAME, ACTION, CLEAR, ALL);
        }
    }

    public static class ScheduledDownloads {
        public static final String EVENT_NAME = "Scheduled Downloads";
        private static final String CLICK_ON_INSTALL_SELECTED = "Clicked on Install Selected";
        private static final String CLICK_ON_INVERT_SELECTION = "Clicked on Invert Selection";
        private static final String CLICK_ON_REMOVE_SELECTED = "Clicked on Remove Selected";

        public static void clickOnInstallSelected() {
//            track(EVENT_NAME, ACTION, CLICK_ON_INSTALL_SELECTED, ALL);
        }

        public static void clickOnInvertSelection() {
//            track(EVENT_NAME, ACTION, CLICK_ON_INVERT_SELECTION, ALL);
        }

        public static void clickOnRemoveSelected() {
//            track(EVENT_NAME, ACTION, CLICK_ON_REMOVE_SELECTED, ALL);
        }
    }

    public static class SendFeedback {

        public static final String EVENT_NAME = "Send Feedback";
        private static final String SEND_FEEDBACK = EVENT_NAME;

        public static void sendFeedback() {
            track(EVENT_NAME, ACTION, SEND_FEEDBACK, ALL);
        }
    }

    public static class ExcludedUpdates {
        private static final String EVENT_NAME = "Excluded Updates";
        private static final String RESTORE_UPDATES = "Restore Updates";

        public static void restoreUpdates() {
            track(EVENT_NAME, ACTION, RESTORE_UPDATES, ALL);
        }
    }

    /**
     * Incomplete
     */
    public static class Settings {

        public static final String EVENT_NAME = "Settings";
        private static final String CHECKED = "Checked";

        public static void onSettingChange(String s) {
//            track(EVENT_NAME, ACTION, s, ALL);
        }

        public static void onSettingChange(String s, boolean checked) {
//            track(EVENT_NAME, ACTION, s, ALL);
//
//            HashMap<String, String> objectObjectHashMap = new HashMap<>();
//            objectObjectHashMap.put(ACTION, s);
//            objectObjectHashMap.put(CHECKED, Boolean.valueOf(checked).toString());
        }
    }

    public static class Facebook {

        public static final String EVENT_NAME = "Facebook";

        public static final String JOIN = "Join";
        public static final String LOGIN = "Login";

        public static void join() {
            track(EVENT_NAME, ACTION, JOIN, ALL);
        }

        public static void Login() {
            track(EVENT_NAME, ACTION, LOGIN, ALL);
        }
    }

    public static class BackupApps {
        public static final String EVENT_NAME = "Opened Backup Apps";

        public static void open() {
            track(EVENT_NAME, ALL);
        }
    }

    public static class Home {
        public static final String EVENT_NAME = "Home";

        public static final String CLICK_ON_MORE_ = "Click on More ";
        public static final String CLICK_ON_EDITORS_CHOISE = "Click On Editor's Choise";
        public static final String CLICK_ON_HIGHLIGHTED = "Click On Highlighted";
        public static final String CLICK_ON_HIGHLIGHTED_MORE = "Click On Highlighted More";
        public static final String CLICK_ON_APPLICATIONS = "Click On Applications";
        public static final String CLICK_ON_APPLICATIONS_MORE = "Click On Applications More";
        public static final String CLICK_ON_GAMES = "Click On Games";
        public static final String CLICK_ON_GAMES_MORE = "Click On Games More";
        public static final String CLICK_ON_REVIEWS = "Click On Reviews";
        public static final String CLICK_ON_REVIEWS_MORE = "Click On Reviews More";
        public static final String CLICK_ON_PUBLISHERS = "Click On Publishers";
        public static final String CLICK_ON_PUBLISHERS_MORE = "Click On Publishers More";
        public static final String CLICK_ON_APPS_ESSENTIALS = "Click On Apps Essentials";
        public static final String CLICK_ON_APPS_FOR_KIDS = "Click On Apps For Kids";

        public static void clickOnHighlighted() {
//            track(EVENT_NAME, ACTION, CLICK_ON_HIGHLIGHTED_MORE, ALL);
        }

        public static void generic(String s) {
            track(EVENT_NAME, ACTION, s, ALL);
        }

//        public static void clickOnApplicationsMore() {
//            track(EVENT_NAME, ACTION, CLICK_ON_APPLICATIONS_MORE, ALL);
//        }

        public static void clickOnReviewsMore() {
//            track(EVENT_NAME, ACTION, CLICK_ON_REVIEWS_MORE, ALL);
        }

        public static void clickOnMoreWidget(String widgetname) {
//            track(EVENT_NAME, ACTION, CLICK_ON_MORE_ + widgetname, ALL);
        }
    }

    public static class AdultContent {

        public static final String EVENT_NAME = "Adult Content";

        public static final String UNLOCK = "false";
        public static final String LOCK = "true";
        public static void lock() {
            HashMap<String,String> map = new HashMap<>();
            map.put(ACTION, LOCK);
            FlurryAgent.logEvent(EVENT_NAME, map);

            Logger.d(AdultContent.class.getName(), "TEST");


//            track(EVENT_NAME, ACTION, "Click on On", ALL);
        }

        public static void unlock() {
            HashMap<String,String> map = new HashMap<>();
            map.put(ACTION, UNLOCK);
            FlurryAgent.logEvent(EVENT_NAME, map);

            Logger.d(AdultContent.class.getName(), "TEST");

//            track(EVENT_NAME, ACTION, "Click on Off", ALL);
        }
    }

    public static class Top {
        public static final String EVENT_NAME = "Top";

        public static final String CLICK_ON_LOCAL_TOP_APPS_MORE = "Click on Local Top Apps More";
        public static final String CLICK_ON_TOP_APPLICATIONS_MORE = "Click on Top Applications More";
        public static final String CLICK_ON_LOCAL_TOP_STORES_MORE = "Click on Local Top Stores More";

    }

    public static class Stores {
        public static final String EVENT_NAME = "Stores";

        public static final String STORE_NAME = "Store Name";

        public static void enter(String storeName) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(ACTION, "Enter");
                map.put(STORE_NAME, storeName);

                track(EVENT_NAME, map, ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void subscribe(String storeName) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(ACTION, "Subscribe");
                map.put(STORE_NAME, storeName);

                track(EVENT_NAME, map, ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class Updates {
        public static final String EVENT_NAME = "Updates";

        public static final String CLICKED_ON_CREATE_REVIEW = "Create Review";
        public static final String CLICKED_ON_UPDATE = "Update";
        public static final String CLICKED_ON_UPDATE_ALL = "Update All";

        public static void update() {
            track(EVENT_NAME, ACTION, CLICKED_ON_UPDATE, ALL);
        }

        public static void updateAll() {
            track(EVENT_NAME, ACTION, "Update All", ALL);
        }

        public static void createReview() {
            track(EVENT_NAME, ACTION, CLICKED_ON_CREATE_REVIEW, ALL);
        }
    }

    // TODO: Não está implementado na v6
    public static class DownloadManager {
        public static final String EVENT_NAME = "Download Manager";

        public static void clearDownloadComplete() {
//            track(EVENT_NAME, ACTION, "Clear download complete", ALL);
        }

        public static void clickDownloadComplete() {
//            track(EVENT_NAME, ACTION, "Click download complete", ALL);
        }

        public static void clearTopMenu() {
//            track(EVENT_NAME, ACTION, "Clear topmenu", ALL);
        }
    }

    public static class Search {
        //event names
        public static final String EVENT_NAME_SEARCH_TERM = "Search Term";
        public static final String EVENT_NAME_NO_SEARCH_RESULTS = "No Search Result";

        //event attributes
        public static final String QUERY = "Query";

        public static void searchTerm(String query) {
            track(EVENT_NAME_SEARCH_TERM, QUERY, query, ALL);
        }

        public static void noSearchResults(String query) {
            HashMap<String,String> map = new HashMap<>();
            map.put(QUERY, query);
            FlurryAgent.logEvent(EVENT_NAME_NO_SEARCH_RESULTS, map);
            track(EVENT_NAME_NO_SEARCH_RESULTS, QUERY, query, ALL);
        }
    }

    public static class ApplicationInstall {
        public static final String EVENT_NAME = "Application Install";

        private static final String TYPE = "Type";
        private static final String PACKAGE_NAME = "Package Name";
        private static final String REFERRED = "Referred";
        private static final String TRUSTED_BADGE = "Trusted Badge";

        private static final String REPLACED = "Replaced";
        private static final String INSTALLED = "Installed";
        private static final String DOWNGRADED_ROLLBACK = "Downgraded Rollback";

        private static void innerTrack(String packageName, String type, String trustedBadge, int flags) {
            try {
                HashMap<String, String> stringObjectHashMap = new HashMap<>();

                stringObjectHashMap.put(TRUSTED_BADGE, trustedBadge);
                stringObjectHashMap.put(TYPE, type);
                stringObjectHashMap.put(PACKAGE_NAME, packageName);

                FlurryAgent.logEvent(EVENT_NAME, stringObjectHashMap);

                track(EVENT_NAME, stringObjectHashMap, flags);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void installed(String packageName, String trustedBadge) {
            innerTrack(packageName, INSTALLED, trustedBadge, ALL);
        }

        public static void replaced(String packageName) {
            innerTrack(packageName, REPLACED, null, ALL);
        }

        public static void downgraded(String packageName) {
            innerTrack(packageName, DOWNGRADED_ROLLBACK, null, ALL);
        }
    }

    public static class ApplicationLaunch {

        public static final String EVENT_NAME = "Application Launch";
        public static final String SOURCE = "Source";
        public static final String LAUNCHER = "Launcher";
        public static final String WEBSITE = "Website";
        public static final String NEW_UPDATES_NOTIFICATION = "New Updates Available";
        public static final String DOWNLOADING_UPDATES = "Downloading Updates";
        public static final String TIMELINE_NOTIFICATION = "Timeline Notification";
        public static final String NEW_REPO = "New Repository";
        public static final String URI = "Uri";

        public static void launcher() {
            track(EVENT_NAME, SOURCE, LAUNCHER, ALL);

        }

        public static void website(String uri) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(SOURCE, WEBSITE);

                if (uri != null) {
                    map.put(URI, uri.substring(0, uri.indexOf(":")));
                }

                track(EVENT_NAME, map, ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void newUpdatesNotification() {
            track(EVENT_NAME, SOURCE, NEW_UPDATES_NOTIFICATION, ALL);
        }

        public static void downloadingUpdates() {
            track(EVENT_NAME, SOURCE, DOWNLOADING_UPDATES, ALL);
        }

        public static void timelineNotification() {
            track(EVENT_NAME, SOURCE, TIMELINE_NOTIFICATION, ALL);
        }

        public static void newRepo() {
            track(EVENT_NAME, SOURCE, NEW_REPO, ALL);
        }
    }

    public static class ClickedOnInstallButton {
        private static final String EVENT_NAME = "Clicked on install button";

        //attributes
        private static final String APPLICATION_NAME = "Application Name";
        private static final String WARNING = "Warning";
    
        public static void clicked(GetAppMeta.App app) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(APPLICATION_NAME, app.getPackageName());
//                map.put(WARNING, app.getFile().getMalware().getRank().name());

                FlurryAgent.logEvent(EVENT_NAME, map);

                track(EVENT_NAME, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class DownloadComplete {
        public static final String EVENT_NAME = "Download Complete";
        private static final String PACKAGE_NAME = "Package Name";
        private static final String TRUSTED_BADGE = "Trusted Badge";

        public static void downloadComplete(GetAppMeta.App app) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(PACKAGE_NAME, app.getPackageName());
                map.put(TRUSTED_BADGE, app.getFile().getMalware().getRank().name());

                FlurryAgent.logEvent(EVENT_NAME, map);

                track(EVENT_NAME, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class SocialTimeline {

        public static final String EVENT_NAME = "Social Timeline";

        public static final String eventName = "Social Timeline";
        public static final String APPLICATION_NAME = "Application Name";
        public static final String action = "Action";
        public static final String like = "Like";
        public static final String DISLIKE = "Dislike";
        public static final String comment = "Comment";
        public static final String login = "Login";
        public static final String share = "Share";

        public static void like(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, like);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void dislike(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, DISLIKE);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void comment(String appName) {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(action, comment);
                map.put(APPLICATION_NAME, appName);

                track(eventName, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void login() {
            // Declarado mas não utilizado, apenas para localização
            try {
//                track(eventName, action, login, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ViewedApplication {

        public static final String EVENT_NAME = "Viewed Application";

        private static final String APPLICATION_NAME = "Application Name";
        private static final String TYPE = "Type";
        private static final String APPLICATION_PUBLISHER = "Application Publisher";
        private static final String SOURCE = "Source";
        private static final String TRUSTED_BADGE = "Trusted Badge";

        public static void view(String packageName, String trustedBadge) {
            try {
                HashMap<String, String> map = new HashMap<>();

                map.put(APPLICATION_NAME, packageName);
                map.put(TRUSTED_BADGE, trustedBadge);
                //TODO MISSING POP_UP AB TESTING

                track(EVENT_NAME, map, ALL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Dimensions {

        private static void setDimension(int i, String s) {
            if (!ACTIVATE) {
                return;
            }

            Logger.d("Analytics", "Dimension: " + i + ", Value: " + s);

            Localytics.setCustomDimension(i, s);
        }

        public static void setPartnerDimension(String partner) {
            setDimension(1, partner);
        }

        public static void setVerticalDimension(String verticalName) {
            setDimension(2, verticalName);
        }

        public static void setGmsPresent(boolean b) {
            if (b) {
                setDimension(3, "GMS Present");
            } else {
                setDimension(3, "GMS Not Present");
            }
        }

        public static class Vertical {
            public static final String SMARTPHONE = "smartphone";
        }
    }

    public static class LTV {
        public static void cpi(String packageName) {
            ltv("CPI Click", packageName);
        }

//        public static void purchasedApp(String packageName, double revenue) {
//            ltv("App Purchase", packageName, revenue);
//        }

        private static void ltv(String eventName, String packageName) {
            if (!ACTIVATE) {
                return;
            }

            try {
                HashMap<String, String> map = new HashMap<>();

//                Double revenueDouble = Double.valueOf(revenue);
//                Long value = revenueDouble.longValue();

                map.put("packageName", packageName);

                Logger.d("Analytics", "LTV: " + eventName + ": " + packageName);

                Localytics.tagEvent(eventName, map);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
