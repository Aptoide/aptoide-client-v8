package cm.aptoide.pt.smart.appfiltering;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class AddHelper {
    private static final String DATALIST = "datalist";
    private static final String LIST = "list";
    private static final String PACKAGE = "package";
    private static String appType;
    private static String appCategory;

    // These are all Group IDs.
    // There doesn't seem to be a simple Group ID -> Group Name lookup, so we have to use these
    // values instead. However, the Group IDs are unique to each category & store, and they are
    // fixed, so we don't have to worry about them changing later.
    // The first set are used for the Debug store (aptoide-test-store), while the second set come from
    // the release store (smarttech-iq).
    private static final String APP_EDUCATION_GROUP_ID_DEBUG = "14159143";
    private static final String APP_PRODUCTIVITY_GROUP_ID_DEBUG = "14159390";
    private static final String APP_TOOLS_GROUP_ID_DEBUG = "14033742";
    private static final String APP_BUSINESS_GROUP_ID_DEBUG = "14159261";
    private static final String APP_TRAVEL_AND_LOCAL_GROUP_ID_DEBUG = "14175948";
    private static final String APP_VIDEO_PLAYERS_AND_EDITORS_GROUP_ID_DEBUG = "14025618";
    private static final String APP_COMMUNICATION_GROUP_ID_DEBUG = "14025675";
    private static final String APP_ALL_GROUP_ID_DEBUG = "14008178";
    private static final String GAME_EDUCATION_GROUP_ID_DEBUG = "14159172";
    private static final String GAME_ALL_GROUP_ID_DEBUG = "14008177";

    private static final String APP_EDUCATION_GROUP_ID_RELEASE = "13807552";
    private static final String APP_PRODUCTIVITY_GROUP_ID_RELEASE = "14187193";
    private static final String APP_TOOLS_GROUP_ID_RELEASE = "14176026";
    private static final String APP_BUSINESS_GROUP_ID_RELEASE = "13786921";
    private static final String APP_TRAVEL_AND_LOCAL_GROUP_ID_RELEASE = "14176025";
    private static final String APP_VIDEO_PLAYERS_AND_EDITORS_GROUP_ID_RELEASE = "13807460";
    private static final String APP_COMMUNICATION_GROUP_ID_RELEASE = "13807461";
    private static final String APP_ALL_GROUP_ID_RELEASE = "13786917";
    private static final String GAME_EDUCATION_GROUP_ID_RELEASE = "14176027";
    private static final String GAME_CASUAL_GROUP_ID_RELEASE = "14104788";
    private static final String GAME_ALL_GROUP_ID_RELEASE = "13786916";

    public static final String APP_TYPE_APPLICATION = "Application";
    public static final String APP_TYPE_GAME = "Game";
    // The "All" type is used on the main page, for top & latest downloads.
    public static final String APP_TYPE_ALL = "All";

    public static final String APP_CATEGORY_EDUCATION = "Education";
    public static final String APP_CATEGORY_PRODUCTIVITY = "Productivity";
    public static final String APP_CATEGORY_TOOLS = "Tools";
    public static final String APP_CATEGORY_BUSINESS = "Business";
    public static final String APP_CATEGORY_TRAVEL_LOCAL = "Travel and Local";
    public static final String APP_CATEGORY_VIDEO_PLAYERS_EDITORS = "Video Players and Editors";
    public static final String APP_CATEGORY_COMMUNICATION = "Communication";
    public static final String APP_CATEGORY_CASUAL = "Casual";
    // The "All" category is at the bottom of the page for each type of program (Application & Game). All apps show up here.
    public static final String APP_CATEGORY_ALL = "All";

    public static final String APP_CATEGORY_LATEST = "Latest";
    public static final String APP_CATEGORY_TOP = "Top";

    public static final String URI_GROUP_ID_PARAMETER = "group_id";
    public static final String URI_DOWNLOADS7D_PARAMETER = "downloads7d";

    @Nullable
    JSONObject filterResponse(String originalPath, String jsonResponse, List<AppToAdd> addingList) {
        try {
            JSONObject jObject = new JSONObject(jsonResponse);
            JSONObject dataListObject = jObject.getJSONObject(DATALIST);
            if (dataListObject != null)
            {
                JSONArray jArray = dataListObject.getJSONArray(LIST);
                if (addingList != null && jArray != null) {
                    // The group_id is embedded in the URL with slashes surrounding it. As such, it's
                    // not a JSON parameter, and it's also not a traditional URI parameter (i.e. it doesn't
                    // contain a '?' followed by '&'s for each additional parameter).

                    if (originalPath.contains(URI_GROUP_ID_PARAMETER)) {
                        if (originalPath.contains(APP_EDUCATION_GROUP_ID_DEBUG) || originalPath.contains(APP_EDUCATION_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_EDUCATION;
                        } else if (originalPath.contains(APP_PRODUCTIVITY_GROUP_ID_DEBUG) || originalPath.contains(APP_PRODUCTIVITY_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_PRODUCTIVITY;
                        } else if (originalPath.contains(APP_TOOLS_GROUP_ID_DEBUG) || originalPath.contains(APP_TOOLS_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_TOOLS;
                        } else if (originalPath.contains(APP_BUSINESS_GROUP_ID_DEBUG) || originalPath.contains(APP_BUSINESS_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_BUSINESS;
                        } else if (originalPath.contains(APP_TRAVEL_AND_LOCAL_GROUP_ID_DEBUG) || originalPath.contains(APP_TRAVEL_AND_LOCAL_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_TRAVEL_LOCAL;
                        } else if (originalPath.contains(APP_VIDEO_PLAYERS_AND_EDITORS_GROUP_ID_DEBUG) || originalPath.contains(APP_VIDEO_PLAYERS_AND_EDITORS_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_VIDEO_PLAYERS_EDITORS;
                        } else if (originalPath.contains(APP_COMMUNICATION_GROUP_ID_DEBUG) || originalPath.contains(APP_COMMUNICATION_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_COMMUNICATION;
                        } else if (originalPath.contains(APP_ALL_GROUP_ID_DEBUG) || originalPath.contains(APP_ALL_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_APPLICATION;
                            appCategory = APP_CATEGORY_ALL;
                        } else if (originalPath.contains(GAME_EDUCATION_GROUP_ID_DEBUG) || originalPath.contains(GAME_EDUCATION_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_GAME;
                            appCategory = APP_CATEGORY_EDUCATION;
                        } else if (originalPath.contains(GAME_CASUAL_GROUP_ID_RELEASE)) {
                            // We don't have this category in our debug store - only the release one.
                            appType = APP_TYPE_GAME;
                            appCategory = APP_CATEGORY_CASUAL;
                        } else if (originalPath.contains(GAME_ALL_GROUP_ID_DEBUG) || originalPath.contains(GAME_ALL_GROUP_ID_RELEASE)) {
                            appType = APP_TYPE_GAME;
                            appCategory = APP_CATEGORY_ALL;
                        }
                    } else if (originalPath.contains(URI_DOWNLOADS7D_PARAMETER)) {
                        appType = APP_TYPE_ALL;
                        appCategory = APP_CATEGORY_TOP;
                    } else {
                        appType = APP_TYPE_ALL;
                        appCategory = APP_CATEGORY_LATEST;
                    }

                    for (int addingIndex = 0; addingIndex < addingList.size(); addingIndex++) {
                        AppToAdd appToAdd = addingList.get(addingIndex);

                        // Here's the logic - we include the app from our .json file if:
                        // 1) The type & category listed in the .json file match the type & category we're currently processing.
                        // 2) We've matched the type (Application or Game) & we're in the "All" category at the bottom.
                        // 3) We're in the "Top" section and we want to include it there (set via a boolean in the .json)
                        // 4) We're in the "Latest" section and we want to include it there (set via a boolean in the .json)

                        if ((appToAdd.getAppType().equals(appType)) || (appType.equals(APP_TYPE_ALL))) {
                            if ((appToAdd.getAppCategory().equals(appCategory)) ||
                                    (appCategory.equals(APP_CATEGORY_TOP) && appToAdd.getIncludeInTop()) ||
                                    (appCategory.equals(APP_CATEGORY_LATEST) && appToAdd.getIncludeInLatest()) ||
                                    (appCategory.equals(APP_CATEGORY_ALL))) {
                                JSONObject jsonObjectToAdd = new JSONObject(appToAdd.getJson());
                                jArray.put(jsonObjectToAdd);
                            }
                        }
                    }
                }
            }
            return jObject;
        } catch (Exception e) {
            return null;
        }
    }
}